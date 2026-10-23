package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import system_gym.model.FichaMedica;
import system_gym.service.ClienteService;
import system_gym.service.FichaMedicaService;
import system_gym.service.UsuarioService;

@Controller
@RequestMapping("/fichas-medica")
public class FichaMedicaController {

    private final FichaMedicaService fichaMedicaService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    public FichaMedicaController(FichaMedicaService fichaMedicaService,
            ClienteService clienteService, UsuarioService usuarioService) {
        this.fichaMedicaService = fichaMedicaService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("fichas", fichaMedicaService.listar());
        return "fichas-medica/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("ficha", new FichaMedica());
        cargarCatalogos(model);
        return "fichas-medica/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute("ficha") FichaMedica ficha,
            @RequestParam Integer clienteId, @RequestParam Integer entrenadorId) {
        ficha.setCliente(clienteService.buscarPorId(clienteId));
        ficha.setEntrenador(usuarioService.buscarPorId(entrenadorId));
        fichaMedicaService.guardar(ficha);
        return "redirect:/fichas-medica";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("ficha", fichaMedicaService.buscarPorId(id));
        cargarCatalogos(model);
        return "fichas-medica/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id) {
        fichaMedicaService.eliminar(id);
        return "redirect:/fichas-medica";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("entrenadores", usuarioService.listar());
    }
}
