package system_gym.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import system_gym.reporte.ExportadorReporte;
import system_gym.reporte.FormatoReporte;
import system_gym.reporte.ReporteData;
import system_gym.reporte.ReporteFiltro;
import system_gym.reporte.ReporteTipo;
import system_gym.service.ClienteService;
import system_gym.service.ReporteService;
import system_gym.service.UsuarioService;

/**
 * Controlador de reportes: previsualización y exportación en varios formatos.
 */
@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private static final DateTimeFormatter MARCA_TIEMPO = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");

    private final ReporteService reporteService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;
    private final Map<FormatoReporte, ExportadorReporte> exportadores = new EnumMap<>(FormatoReporte.class);

    public ReporteController(ReporteService reporteService, ClienteService clienteService,
            UsuarioService usuarioService, List<ExportadorReporte> exportadores) {
        this.reporteService = reporteService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
        exportadores.forEach(exportador -> this.exportadores.put(exportador.formato(), exportador));
    }

    @GetMapping
    public String index(Model model, Authentication authentication) {
        boolean esAdmin = tieneRol(authentication, "ROLE_ADMIN");
        boolean esRecepcionista = tieneRol(authentication, "ROLE_RECEPCIONISTA");
        boolean esEntrenador = tieneRol(authentication, "ROLE_ENTRENADOR");
        model.addAttribute("reportes", ReporteTipo.disponibles(esAdmin, esRecepcionista, esEntrenador));
        return "reportes/index";
    }

    @GetMapping("/{slug}")
    public String ver(@PathVariable String slug, @ModelAttribute("filtro") ReporteFiltro filtro,
            Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        ReporteTipo tipo = ReporteTipo.desdeSlug(slug).orElse(null);
        if (tipo == null) {
            redirectAttributes.addFlashAttribute("error", "El reporte solicitado no existe.");
            return "redirect:/reportes";
        }
        if (!autorizado(tipo, authentication)) {
            redirectAttributes.addFlashAttribute("error", "No tiene permiso para consultar este reporte.");
            return "redirect:/reportes";
        }

        model.addAttribute("tipo", tipo);
        model.addAttribute("reporte", reporteService.generar(tipo, filtro));
        agregarDatosDeFiltros(model, tipo == ReporteTipo.FALLAS);
        return "reportes/detalle";
    }

    @GetMapping("/{slug}/exportar")
    public void exportar(@PathVariable String slug, @ModelAttribute ReporteFiltro filtro,
            @RequestParam(defaultValue = "pdf") String formato,
            @RequestParam(defaultValue = "false") boolean inline,
            Authentication authentication, HttpServletResponse response) throws IOException {
        ReporteTipo tipo = ReporteTipo.desdeSlug(slug).orElse(null);
        if (tipo == null || !autorizado(tipo, authentication)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        exportar(response, reporteService.generar(tipo, filtro), formato, inline,
                "reporte-" + tipo.getSlug());
    }

    @GetMapping("/personalizado")
    public String personalizado(@ModelAttribute("filtro") ReporteFiltro filtro, Model model,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        if (!tieneRol(authentication, "ROLE_ADMIN")) {
            redirectAttributes.addFlashAttribute("error", "Solo el administrador puede crear reportes personalizados.");
            return "redirect:/reportes";
        }
        if (filtro.getTipoPersonalizado() == null || filtro.getTipoPersonalizado().isBlank()) {
            filtro.setTipoPersonalizado(ReporteTipo.MIEMBROS.getSlug());
        }
        model.addAttribute("reporte", reporteService.generarPersonalizado(
                filtro.getTipoPersonalizado(), filtro));
        model.addAttribute("tiposPersonalizables", reporteService.tiposPersonalizables());
        model.addAttribute("columnasDisponibles",
                reporteService.columnasDisponibles(filtro.getTipoPersonalizado()));
        agregarDatosDeFiltros(model, "fallas".equalsIgnoreCase(filtro.getTipoPersonalizado()));
        return "reportes/personalizado";
    }

    @GetMapping("/personalizado/exportar")
    public void exportarPersonalizado(@ModelAttribute ReporteFiltro filtro,
            @RequestParam(defaultValue = "pdf") String formato,
            @RequestParam(defaultValue = "false") boolean inline,
            Authentication authentication, HttpServletResponse response) throws IOException {
        if (!tieneRol(authentication, "ROLE_ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (filtro.getTipoPersonalizado() == null || filtro.getTipoPersonalizado().isBlank()) {
            filtro.setTipoPersonalizado(ReporteTipo.MIEMBROS.getSlug());
        }
        exportar(response, reporteService.generarPersonalizado(filtro.getTipoPersonalizado(), filtro),
                formato, inline, "reporte-personalizado");
    }

    private void exportar(HttpServletResponse response, ReporteData reporte, String formato,
            boolean inline, String prefijo) throws IOException {
        FormatoReporte formatoReporte;
        try {
            formatoReporte = FormatoReporte.desde(formato);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
        ExportadorReporte exportador = exportadores.get(formatoReporte);
        if (exportador == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No hay un exportador disponible para el formato solicitado.");
            return;
        }
        byte[] contenido = exportador.exportar(reporte);
        boolean esInline = inline && formatoReporte == FormatoReporte.PDF;
        String nombre = prefijo + "-" + LocalDateTime.now().format(MARCA_TIEMPO)
                + "." + formatoReporte.getExtension();
        response.setContentType(formatoReporte.getTipoContenido());
        response.setHeader("Content-Disposition",
                (esInline ? "inline" : "attachment") + "; filename=\"" + nombre + "\"");
        response.setContentLength(contenido.length);
        response.getOutputStream().write(contenido);
    }

    private void agregarDatosDeFiltros(Model model, boolean responsablesTodos) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("responsables", responsablesTodos
                ? usuarioService.listar()
                : usuarioService.listarEntrenadores());
    }

    private boolean autorizado(ReporteTipo tipo, Authentication authentication) {
        return tipo.puedeVer(tieneRol(authentication, "ROLE_ADMIN"),
                tieneRol(authentication, "ROLE_RECEPCIONISTA"),
                tieneRol(authentication, "ROLE_ENTRENADOR"));
    }

    private boolean tieneRol(Authentication authentication, String autoridad) {
        return authentication.getAuthorities().stream()
                .anyMatch(rol -> autoridad.equals(rol.getAuthority()));
    }
}
