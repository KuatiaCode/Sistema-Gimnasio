package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import system_gym.model.Configuracion;
import system_gym.service.ConfiguracionService;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    public ConfiguracionController(ConfiguracionService configuracionService) {
        this.configuracionService = configuracionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("configuraciones", configuracionService.listar());
        return "configuracion/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("configuracion", new Configuracion());
        return "configuracion/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Configuracion configuracion) {
        configuracionService.guardar(configuracion);
        return "redirect:/configuracion";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("configuracion", configuracionService.buscarPorId(id));
        return "configuracion/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id) {
        configuracionService.eliminar(id);
        return "redirect:/configuracion";
    }
}
