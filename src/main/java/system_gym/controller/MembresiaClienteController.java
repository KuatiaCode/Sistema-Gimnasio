package system_gym.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import system_gym.model.MembresiaCliente;
import system_gym.model.MembresiaEvento;
import system_gym.model.PlanesMembresia;
import system_gym.service.ClienteService;
import system_gym.service.MembresiaClienteService;
import system_gym.service.MembresiaComprobanteService;
import system_gym.service.PlanesMembresiaService;

/**
 * Controlador MVC para las membresías contratadas por los clientes.
 */
@Controller
@RequestMapping("/membresias")
public class MembresiaClienteController {

    private final MembresiaClienteService membresiaClienteService;
    private final MembresiaComprobanteService membresiaComprobanteService;
    private final ClienteService clienteService;
    private final PlanesMembresiaService planesMembresiaService;

    public MembresiaClienteController(MembresiaClienteService membresiaClienteService,
            MembresiaComprobanteService membresiaComprobanteService,
            ClienteService clienteService, PlanesMembresiaService planesMembresiaService) {
        this.membresiaClienteService = membresiaClienteService;
        this.membresiaComprobanteService = membresiaComprobanteService;
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

    @GetMapping("/{id}/historial")
    public String historial(@PathVariable Integer id, Model model) {
        model.addAttribute("membresia", membresiaClienteService.buscarPorId(id));
        model.addAttribute("eventos", membresiaClienteService.listarEventos(id));
        return "membresias/historial";
    }

    @GetMapping("/{id}/renovar")
    public String renovarForm(@PathVariable Integer id, Model model,
            RedirectAttributes redirectAttributes) {
        MembresiaCliente membresia = membresiaClienteService.buscarPorId(id);
        String error = validarAccion(membresia, "renovar");
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/membresias/" + id + "/historial";
        }
        LocalDate hoy = LocalDate.now();
        LocalDate sugerida = membresia.getFechaVencimiento() == null
                ? hoy.plusMonths(1)
                : membresia.getFechaVencimiento().plusMonths(1);
        if (sugerida.isBefore(hoy)) {
            sugerida = hoy.plusMonths(1);
        }
        model.addAttribute("membresia", membresia);
        model.addAttribute("accion", "renovar");
        model.addAttribute("fechaSugerida", sugerida);
        return "membresias/gestion";
    }

    @PostMapping("/{id}/renovar")
    public String renovar(@PathVariable Integer id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento,
            @RequestParam(required = false) String motivo,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            MembresiaEvento evento = membresiaClienteService.renovar(id, fechaVencimiento,
                    motivo, usuario(authentication));
            redirectAttributes.addFlashAttribute("mensaje", "Membresía renovada correctamente.");
            redirectAttributes.addFlashAttribute("eventoId", evento.getId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/membresias/" + id + "/renovar";
        }
        return "redirect:/membresias/" + id + "/historial";
    }

    @GetMapping("/{id}/cancelar")
    public String cancelarForm(@PathVariable Integer id, Model model,
            RedirectAttributes redirectAttributes) {
        MembresiaCliente membresia = membresiaClienteService.buscarPorId(id);
        String error = validarAccion(membresia, "cancelar");
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/membresias/" + id + "/historial";
        }
        model.addAttribute("membresia", membresia);
        model.addAttribute("accion", "cancelar");
        model.addAttribute("fechaSugerida", LocalDate.now());
        return "membresias/gestion";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Integer id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCancelacion,
            @RequestParam(required = false) String motivo,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            MembresiaEvento evento = membresiaClienteService.cancelar(id, fechaCancelacion,
                    motivo, usuario(authentication));
            redirectAttributes.addFlashAttribute("mensaje", "Membresía cancelada correctamente.");
            redirectAttributes.addFlashAttribute("eventoId", evento.getId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/membresias/" + id + "/cancelar";
        }
        return "redirect:/membresias/" + id + "/historial";
    }

    @GetMapping("/{id}/congelar")
    public String congelarForm(@PathVariable Integer id, Model model,
            RedirectAttributes redirectAttributes) {
        MembresiaCliente membresia = membresiaClienteService.buscarPorId(id);
        String error = validarAccion(membresia, "congelar");
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/membresias/" + id + "/historial";
        }
        model.addAttribute("membresia", membresia);
        model.addAttribute("accion", "congelar");
        model.addAttribute("fechaSugerida", LocalDate.now());
        model.addAttribute("fechaFinSugerida", LocalDate.now().plusDays(7));
        return "membresias/gestion";
    }

    @PostMapping("/{id}/congelar")
    public String congelar(@PathVariable Integer id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String motivo,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            MembresiaEvento evento = membresiaClienteService.congelar(id, fechaInicio, fechaFin,
                    motivo, usuario(authentication));
            redirectAttributes.addFlashAttribute("mensaje", "Membresía congelada correctamente.");
            redirectAttributes.addFlashAttribute("eventoId", evento.getId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/membresias/" + id + "/congelar";
        }
        return "redirect:/membresias/" + id + "/historial";
    }

    @PostMapping("/{id}/descongelar")
    public String descongelar(@PathVariable Integer id, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            MembresiaEvento evento = membresiaClienteService.descongelar(id, usuario(authentication));
            redirectAttributes.addFlashAttribute("mensaje", "Membresía reactivada correctamente.");
            redirectAttributes.addFlashAttribute("eventoId", evento.getId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/membresias/" + id + "/historial";
    }

    @GetMapping("/{id}/comprobante/{eventoId}")
    public void comprobante(@PathVariable Integer id, @PathVariable Integer eventoId,
            @RequestParam(defaultValue = "false") boolean inline,
            HttpServletResponse response) throws IOException {
        try {
            byte[] contenido = membresiaComprobanteService.generar(id, eventoId);
            String nombre = "comprobante-membresia-" + id + "-" + eventoId + ".pdf";
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    (inline ? "inline" : "attachment") + "; filename=\"" + nombre + "\"");
            response.setContentLength(contenido.length);
            response.getOutputStream().write(contenido);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private String validarAccion(MembresiaCliente membresia, String accion) {
        Short estado = membresia.getEstado();
        if ("renovar".equals(accion) && (estado != null && (estado == 3 || estado == 4))) {
            return estado == 3
                    ? "Descongelá la membresía antes de renovarla."
                    : "Una membresía inactiva no se puede renovar.";
        }
        if ("cancelar".equals(accion) && estado != null && estado == 4) {
            return "La membresía ya está inactiva.";
        }
        if ("congelar".equals(accion)) {
            if (estado == null || estado != 1) {
                return "Solo se puede congelar una membresía activa.";
            }
            if (membresia.getFechaVencimiento() != null
                    && membresia.getFechaVencimiento().isBefore(LocalDate.now())) {
                return "La membresía está vencida.";
            }
        }
        return null;
    }

    private String usuario(Authentication authentication) {
        return authentication == null ? "Sistema" : authentication.getName();
    }

    private void cargarCatalogos(Model model, boolean incluirInactivos) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("planes", incluirInactivos
                ? planesMembresiaService.listar()
                : planesMembresiaService.listarActivos());
    }
}
