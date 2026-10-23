package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import system_gym.model.PlanesMembresia;
import system_gym.service.PlanesMembresiaService;

@Controller
@RequestMapping("/planes")
public class PlanesMembresiaController {

    private final PlanesMembresiaService planesMembresiaService;

    public PlanesMembresiaController(PlanesMembresiaService planesMembresiaService) {
        this.planesMembresiaService = planesMembresiaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("planes", planesMembresiaService.listar());
        return "planes/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("plan", new PlanesMembresia());
        return "planes/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute("plan") PlanesMembresia plan) {
        planesMembresiaService.guardar(plan);
        return "redirect:/planes";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("plan", planesMembresiaService.buscarPorId(id));
        return "planes/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id) {
        planesMembresiaService.eliminar(id);
        return "redirect:/planes";
    }
}
