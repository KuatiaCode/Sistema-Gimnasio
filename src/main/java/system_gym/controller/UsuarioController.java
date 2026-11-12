package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import system_gym.model.Usuario;
import system_gym.service.RolesService;
import system_gym.service.UsuarioService;

/**
 * Controlador MVC para los usuarios dentro de Configuración.
 */
@Controller
@RequestMapping("/configuracion/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolesService rolesService;

    public UsuarioController(UsuarioService usuarioService, RolesService rolesService) {
        this.usuarioService = usuarioService;
        this.rolesService = rolesService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        // Se usa otro nombre para no chocar con el campo "usuario" de la entidad.
        model.addAttribute("usuarioForm", new Usuario());
        cargarRoles(model);
        return "usuarios/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute("usuarioForm") Usuario usuario, @RequestParam Integer rolesId,
            @RequestParam(required = false) String password, RedirectAttributes redirectAttributes) {
        usuario.setRoles(rolesService.buscarPorId(rolesId));
        try {
            usuarioService.guardar(usuario, password);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return usuario.getId() == null
                    ? "redirect:/configuracion/usuarios/nuevo"
                    : "redirect:/configuracion/usuarios/" + usuario.getId() + "/editar";
        }
        return "redirect:/configuracion/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("usuarioForm", usuarioService.buscarPorId(id));
        cargarRoles(model);
        return "usuarios/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminar(id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/configuracion/usuarios";
    }

    private void cargarRoles(Model model) {
        model.addAttribute("roles", rolesService.listar());
    }
}
