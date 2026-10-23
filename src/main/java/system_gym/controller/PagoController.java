package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import system_gym.model.Pago;
import system_gym.service.ClienteService;
import system_gym.service.MembresiaClienteService;
import system_gym.service.PagoService;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final ClienteService clienteService;
    private final MembresiaClienteService membresiaClienteService;

    public PagoController(PagoService pagoService, ClienteService clienteService,
            MembresiaClienteService membresiaClienteService) {
        this.pagoService = pagoService;
        this.clienteService = clienteService;
        this.membresiaClienteService = membresiaClienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pagos", pagoService.listar());
        return "pagos/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("pago", new Pago());
        cargarCatalogos(model);
        return "pagos/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Pago pago, @RequestParam Integer clienteId,
            @RequestParam(required = false) Integer membresiaId) {
        pago.setCliente(clienteService.buscarPorId(clienteId));
        if (membresiaId != null) {
            pago.setMembresia(membresiaClienteService.buscarPorId(membresiaId));
        } else {
            pago.setMembresia(null);
        }
        pagoService.guardar(pago);
        return "redirect:/pagos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("pago", pagoService.buscarPorId(id));
        cargarCatalogos(model);
        return "pagos/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id) {
        pagoService.eliminar(id);
        return "redirect:/pagos";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("membresias", membresiaClienteService.listar());
    }
}
