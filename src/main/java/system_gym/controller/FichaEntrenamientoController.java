package system_gym.controller;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import system_gym.model.FichaEntrenamiento;
import system_gym.model.Usuario;
import system_gym.service.ClienteService;
import system_gym.service.FichaEntrenamientoService;
import system_gym.service.MembresiaClienteService;
import system_gym.service.UsuarioService;

/**
 * Controlador MVC para las fichas de entrenamiento.
 */
@Controller
@RequestMapping("/fichas-entrenamiento")
public class FichaEntrenamientoController {

    private final FichaEntrenamientoService fichaEntrenamientoService;
    private final ClienteService clienteService;
    private final MembresiaClienteService membresiaClienteService;
    private final UsuarioService usuarioService;

    public FichaEntrenamientoController(FichaEntrenamientoService fichaEntrenamientoService,
            ClienteService clienteService, MembresiaClienteService membresiaClienteService,
            UsuarioService usuarioService) {
        this.fichaEntrenamientoService = fichaEntrenamientoService;
        this.clienteService = clienteService;
        this.membresiaClienteService = membresiaClienteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("fichas", fichaEntrenamientoService.listar());
        return "fichas-entrenamiento/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("ficha", new FichaEntrenamiento());
        cargarCatalogos(model);
        return "fichas-entrenamiento/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute("ficha") FichaEntrenamiento ficha,
            @RequestParam Integer clienteId,
            @RequestParam Integer membresiaId,
            @RequestParam(required = false) List<Integer> entrenadorIds,
            RedirectAttributes redirectAttributes) {
        try {
            ficha.setCliente(clienteService.buscarPorId(clienteId));
            ficha.setMembresia(membresiaClienteService.buscarPorId(membresiaId));

            Set<Usuario> entrenadores = new LinkedHashSet<>();
            if (entrenadorIds != null) {
                for (Integer entrenadorId : entrenadorIds) {
                    Usuario entrenador = usuarioService.buscarPorId(entrenadorId);
                    if (!"ENTRENADOR".equals(entrenador.getRoles().getCodigo())) {
                        throw new IllegalArgumentException("El usuario seleccionado no es entrenador.");
                    }
                    entrenadores.add(entrenador);
                }
            }
            ficha.setEntrenadores(entrenadores);
            fichaEntrenamientoService.guardar(ficha);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return ficha.getId() == null
                    ? "redirect:/fichas-entrenamiento/nuevo"
                    : "redirect:/fichas-entrenamiento/" + ficha.getId() + "/editar";
        }
        return "redirect:/fichas-entrenamiento";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("ficha", fichaEntrenamientoService.buscarPorId(id));
        cargarCatalogos(model);
        return "fichas-entrenamiento/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            fichaEntrenamientoService.eliminar(id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/fichas-entrenamiento";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("membresias", membresiaClienteService.listar());
        model.addAttribute("entrenadores", usuarioService.listarEntrenadores());
    }
}
