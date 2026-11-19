package system_gym.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import system_gym.model.Falla;
import system_gym.model.Usuario;
import system_gym.service.FallaService;
import system_gym.service.UsuarioService;

/**
 * Controlador MVC para el registro y seguimiento de fallas técnicas.
 */
@Controller
@RequestMapping("/fallas")
public class FallaController {

    private final FallaService fallaService;
    private final UsuarioService usuarioService;

    public FallaController(FallaService fallaService, UsuarioService usuarioService) {
        this.fallaService = fallaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("fallas", fallaService.listar());
        return "fallas/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, Authentication authentication) {
        Falla falla = new Falla();
        falla.setReportadoPor(authentication.getName());
        model.addAttribute("falla", falla);
        model.addAttribute("esAdmin", esAdmin(authentication));
        return "fallas/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("falla") Falla falla, BindingResult result,
            @RequestParam(required = false) Integer responsableId,
            Authentication authentication, Model model,
            RedirectAttributes redirectAttributes) {
        boolean esAdmin = esAdmin(authentication);
        boolean esNueva = falla.getId() == null;

        if (!esNueva && !esAdmin) {
            redirectAttributes.addFlashAttribute("error", "Solo el administrador puede modificar una falla.");
            return "redirect:/fallas";
        }

        if (result.hasErrors()) {
            prepararFormulario(model, falla, authentication, esAdmin, responsableId);
            return "fallas/form";
        }

        try {
            Usuario usuario = usuarioService.buscarPorUsuario(authentication.getName());
            fallaService.guardar(falla, usuario, esAdmin, responsableId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            result.reject("falla.error", e.getMessage());
            prepararFormulario(model, falla, authentication, esAdmin, responsableId);
            return "fallas/form";
        }

        redirectAttributes.addFlashAttribute("mensaje",
                esNueva ? "Falla registrada correctamente." : "Falla actualizada correctamente.");
        return "redirect:/fallas";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (!esAdmin(authentication)) {
            redirectAttributes.addFlashAttribute("error", "Solo el administrador puede modificar una falla.");
            return "redirect:/fallas";
        }
        model.addAttribute("falla", fallaService.buscarPorId(id));
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("esAdmin", true);
        return "fallas/form";
    }

    private void prepararFormulario(Model model, Falla falla, Authentication authentication,
            boolean esAdmin, Integer responsableId) {
        if (falla.getId() == null) {
            falla.setReportadoPor(authentication.getName());
        } else {
            try {
                Falla existente = fallaService.buscarPorId(falla.getId());
                falla.setReportadoPor(existente.getReportadoPor());
                falla.setUsuarioReporta(existente.getUsuarioReporta());
                falla.setFechaReporte(existente.getFechaReporte());
                falla.setFechaCierre(existente.getFechaCierre());
                if (falla.getEstado() == null) {
                    falla.setEstado(existente.getEstado());
                }
            } catch (IllegalArgumentException e) {
                falla.setId(null);
                falla.setReportadoPor(authentication.getName());
            }
        }

        if (esAdmin && falla.getId() != null && responsableId != null) {
            try {
                falla.setResponsableAtencion(usuarioService.buscarPorId(responsableId));
            } catch (IllegalArgumentException ignored) {
                falla.setResponsableAtencion(null);
            }
        }

        model.addAttribute("falla", falla);
        model.addAttribute("esAdmin", esAdmin);
        if (esAdmin && falla.getId() != null) {
            model.addAttribute("usuarios", usuarioService.listar());
        }
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(autoridad -> "ROLE_ADMIN".equals(autoridad.getAuthority()));
    }
}
