package system_gym.controller;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import system_gym.model.Roles;
import system_gym.service.RolesService;

/**
 * Controlador MVC para los roles dentro de Configuración.
 */
@Controller
@RequestMapping("/configuracion/roles")
public class RolesController {

    private final RolesService rolesService;

    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("roles", rolesService.listar());
        return "roles/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("rol", new Roles());
        return "roles/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute("rol") Roles rol, RedirectAttributes redirectAttributes) {
        String codigo = rol.getCodigo() == null ? "" : rol.getCodigo().trim().toUpperCase(Locale.ROOT);
        if (codigo.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El código del rol es obligatorio.");
            return rol.getId() == null
                    ? "redirect:/configuracion/roles/nuevo"
                    : "redirect:/configuracion/roles/" + rol.getId() + "/editar";
        }
        if (rolesService.existeCodigo(codigo, rol.getId())) {
            redirectAttributes.addFlashAttribute("error", "Ya existe un rol con ese código.");
            return rol.getId() == null
                    ? "redirect:/configuracion/roles/nuevo"
                    : "redirect:/configuracion/roles/" + rol.getId() + "/editar";
        }
        try {
            rolesService.guardar(rol);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return rol.getId() == null
                    ? "redirect:/configuracion/roles/nuevo"
                    : "redirect:/configuracion/roles/" + rol.getId() + "/editar";
        }
        return "redirect:/configuracion/roles";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("rol", rolesService.buscarPorId(id));
        return "roles/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            rolesService.eliminar(id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/configuracion/roles";
    }
}
