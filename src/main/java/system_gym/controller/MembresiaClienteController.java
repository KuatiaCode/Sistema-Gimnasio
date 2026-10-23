package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import system_gym.model.MembresiaCliente;
import system_gym.service.ClienteService;
import system_gym.service.MembresiaClienteService;
import system_gym.service.PlanesMembresiaService;

@Controller
@RequestMapping("/membresias")
public class MembresiaClienteController {

    private final MembresiaClienteService membresiaClienteService;
    private final ClienteService clienteService;
    private final PlanesMembresiaService planesMembresiaService;

    public MembresiaClienteController(MembresiaClienteService membresiaClienteService,
            ClienteService clienteService, PlanesMembresiaService planesMembresiaService) {
        this.membresiaClienteService = membresiaClienteService;
        this.clienteService = clienteService;
        this.planesMembresiaService = planesMembresiaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("membresias", membresiaClienteService.listar());
        return "membresias/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("membresia", new MembresiaCliente());
        cargarCatalogos(model);
        return "membresias/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute MembresiaCliente membresia,
            @RequestParam Integer clienteId, @RequestParam Integer planId) {
        membresia.setCliente(clienteService.buscarPorId(clienteId));
        membresia.setPlanesMembresia(planesMembresiaService.buscarPorId(planId));
        membresiaClienteService.guardar(membresia);
        return "redirect:/membresias";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("membresia", membresiaClienteService.buscarPorId(id));
        cargarCatalogos(model);
        return "membresias/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id) {
        membresiaClienteService.eliminar(id);
        return "redirect:/membresias";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("planes", planesMembresiaService.listar());
    }
}
