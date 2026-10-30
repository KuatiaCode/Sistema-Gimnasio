package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
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
    public String mostrar(Model model, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        model.addAttribute("configuracion", configuracionService.obtenerConfiguracion());
        return "configuracion/index";
    }

    @PostMapping
    public String guardar(@ModelAttribute Configuracion configuracion, RedirectAttributes redirectAttributes) {
        configuracionService.guardar(configuracion);
        redirectAttributes.addFlashAttribute("mensaje", "Datos guardados correctamente.");
        return "redirect:/configuracion";
    }
}
