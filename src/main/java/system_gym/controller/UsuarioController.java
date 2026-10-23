package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import system_gym.model.Usuario;
import system_gym.service.RolesService;
import system_gym.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
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
        model.addAttribute("usuarioForm", new Usuario());
        cargarRoles(model);
        return "usuarios/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute("usuarioForm") Usuario usuario, @RequestParam Integer rolesId) {
        usuario.setRoles(rolesService.buscarPorId(rolesId));
        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("usuarioForm", usuarioService.buscarPorId(id));
        cargarRoles(model);
        return "usuarios/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }

    private void cargarRoles(Model model) {
        model.addAttribute("roles", rolesService.listar());
    }
}
