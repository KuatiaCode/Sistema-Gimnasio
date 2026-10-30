package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LegacyRedirectController {

    @GetMapping("/roles")
    public String roles() {
        return "redirect:/configuracion/roles";
    }

    @GetMapping("/roles/nuevo")
    public String nuevoRol() {
        return "redirect:/configuracion/roles/nuevo";
    }

    @GetMapping("/roles/{id}/editar")
    public String editarRol(@PathVariable Integer id) {
        return "redirect:/configuracion/roles/" + id + "/editar";
    }

    @GetMapping("/usuarios")
    public String usuarios() {
        return "redirect:/configuracion/usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario() {
        return "redirect:/configuracion/usuarios/nuevo";
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Integer id) {
        return "redirect:/configuracion/usuarios/" + id + "/editar";
    }

    @GetMapping("/configuracion/nuevo")
    public String nuevaConfiguracion() {
        return "redirect:/configuracion";
    }

    @GetMapping("/configuracion/{id}/editar")
    public String editarConfiguracion(@PathVariable Integer id) {
        return "redirect:/configuracion";
    }
}
