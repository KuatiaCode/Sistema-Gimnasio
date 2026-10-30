package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import system_gym.model.FichaEntrenamiento;
import system_gym.service.ClienteService;
import system_gym.service.FichaEntrenamientoService;
import system_gym.service.UsuarioService;

/**
 * Controlador MVC para las fichas de entrenamiento.
 */
@Controller
@RequestMapping("/fichas-entrenamiento")
public class FichaEntrenamientoController {

    private final FichaEntrenamientoService fichaEntrenamientoService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    public FichaEntrenamientoController(FichaEntrenamientoService fichaEntrenamientoService,
            ClienteService clienteService, UsuarioService usuarioService) {
        this.fichaEntrenamientoService = fichaEntrenamientoService;
        this.clienteService = clienteService;
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
            @RequestParam Integer clienteId, @RequestParam Integer entrenadorId) {
        // Se resuelven cliente y entrenador a partir de los ids del formulario.
        ficha.setCliente(clienteService.buscarPorId(clienteId));
        ficha.setEntrenador(usuarioService.buscarPorId(entrenadorId));
        fichaEntrenamientoService.guardar(ficha);
        return "redirect:/fichas-entrenamiento";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("ficha", fichaEntrenamientoService.buscarPorId(id));
        cargarCatalogos(model);
        return "fichas-entrenamiento/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id) {
        fichaEntrenamientoService.eliminar(id);
        return "redirect:/fichas-entrenamiento";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("entrenadores", usuarioService.listar());
    }
}
