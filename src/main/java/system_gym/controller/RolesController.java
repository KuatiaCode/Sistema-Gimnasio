package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import system_gym.model.Roles;
import system_gym.service.RolesService;

@Controller
@RequestMapping("/roles")
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
    public String guardar(@ModelAttribute("rol") Roles rol) {
        rolesService.guardar(rol);
        return "redirect:/roles";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("rol", rolesService.buscarPorId(id));
        return "roles/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id) {
        rolesService.eliminar(id);
        return "redirect:/roles";
    }
}
