package system_gym.controller;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import system_gym.model.MembresiaCliente;
import system_gym.model.PlanesMembresia;
import system_gym.service.ClienteService;
import system_gym.service.MembresiaClienteService;
import system_gym.service.PlanesMembresiaService;

/**
 * Controlador MVC para las membresías contratadas por los clientes.
 */
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
        MembresiaCliente membresia = new MembresiaCliente();
        membresia.setFechaInicio(LocalDate.now());
        membresia.setFechaVencimiento(LocalDate.now().plusMonths(1));
        membresia.setEstado((short) 1);
        model.addAttribute("membresia", membresia);
        cargarCatalogos(model, false);
        return "membresias/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute("membresia") MembresiaCliente membresia,
            @RequestParam Integer clienteId,
            @RequestParam(required = false) List<Integer> planIds,
            RedirectAttributes redirectAttributes) {
        try {
            membresia.setCliente(clienteService.buscarPorId(clienteId));
            Set<PlanesMembresia> planes = new LinkedHashSet<>();
            if (planIds != null) {
                for (Integer planId : planIds) {
                    planes.add(planesMembresiaService.buscarPorId(planId));
                }
            }
            membresia.setPlanes(planes);
            membresiaClienteService.guardar(membresia);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return membresia.getId() == null
                    ? "redirect:/membresias/nuevo"
                    : "redirect:/membresias/" + membresia.getId() + "/editar";
        }
        return "redirect:/membresias";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("membresia", membresiaClienteService.buscarPorId(id));
        cargarCatalogos(model, true);
        return "membresias/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            membresiaClienteService.eliminar(id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/membresias";
    }

    private void cargarCatalogos(Model model, boolean incluirInactivos) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("planes", incluirInactivos
                ? planesMembresiaService.listar()
                : planesMembresiaService.listarActivos());
    }
}
