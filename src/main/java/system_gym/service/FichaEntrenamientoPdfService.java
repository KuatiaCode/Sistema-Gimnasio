package system_gym.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.FichaEntrenamiento;
import system_gym.model.MembresiaCliente;
import system_gym.model.PlanesMembresia;
import system_gym.model.Usuario;
import system_gym.reporte.ExportadorPdf;
import system_gym.reporte.ReporteColumna;
import system_gym.reporte.ReporteData;
import system_gym.reporte.ReporteFormatoUtil;
import system_gym.reporte.TipoDato;
import system_gym.repository.FichaEntrenamientoRepository;

@Service
public class FichaEntrenamientoPdfService {

    private final FichaEntrenamientoRepository fichaEntrenamientoRepository;
    private final ConfiguracionService configuracionService;
    private final ExportadorPdf exportadorPdf;

    public FichaEntrenamientoPdfService(FichaEntrenamientoRepository fichaEntrenamientoRepository,
            ConfiguracionService configuracionService, ExportadorPdf exportadorPdf) {
        this.fichaEntrenamientoRepository = fichaEntrenamientoRepository;
        this.configuracionService = configuracionService;
        this.exportadorPdf = exportadorPdf;
    }

    @Transactional(readOnly = true)
    public byte[] generar(Integer id) {
        FichaEntrenamiento ficha = fichaEntrenamientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ficha de entrenamiento no encontrada con id " + id));

        String gimnasio = configuracionService.obtenerConfiguracion().getNombre();
        if (gimnasio == null || gimnasio.isBlank()) {
            gimnasio = "Sistema de gestión";
        }
        String cliente = nombreCliente(ficha);
        MembresiaCliente membresia = ficha.getMembresia();

        List<Map<String, Object>> filas = new ArrayList<>();
        filas.add(fila("Ficha", "#" + ficha.getId()));
        filas.add(fila("Cliente", cliente));
        filas.add(fila("Documento", ficha.getCliente() == null ? "" : ficha.getCliente().getDocumento()));
        filas.add(fila("Teléfono", ficha.getCliente() == null ? "" : ficha.getCliente().getTelefono()));
        filas.add(fila("Correo", ficha.getCliente() == null ? "" : ficha.getCliente().getCorreo()));
        filas.add(fila("Membresía", membresia == null ? "" : "#" + membresia.getId()));
        filas.add(fila("Estado de la membresía", membresia == null ? "" : membresia.getEstadoTexto()));
        filas.add(fila("Vigencia", membresia == null ? ""
                : ReporteFormatoUtil.fecha(membresia.getFechaInicio()) + " al "
                        + ReporteFormatoUtil.fecha(membresia.getFechaVencimiento())));
        filas.add(fila("Fecha de creación", ReporteFormatoUtil.fechaHora(ficha.getFechaCreacion())));
        filas.add(fila("Objetivo", ficha.getObjetivo()));
        filas.add(fila("Días de entrenamiento", ficha.getDiasSemanaTexto()));
        filas.add(fila("Entrenadores", entrenadores(ficha)));

        if (membresia != null && membresia.getPlanes() != null) {
            for (PlanesMembresia plan : membresia.getPlanes()) {
                filas.add(fila("Plan: " + texto(plan.getNombre()), texto(plan.getDescripcion())));
            }
        }

        List<ReporteColumna> columnas = List.of(
                new ReporteColumna("campo", "Campo", TipoDato.TEXTO, 30),
                new ReporteColumna("detalle", "Detalle", TipoDato.TEXTO, 90));
        ReporteData reporte = new ReporteData(gimnasio,
                "Ficha técnica de entrenamiento",
                "Ficha #" + ficha.getId() + " - " + cliente,
                columnas, filas,
                List.of("Documento generado automáticamente por el sistema.",
                        "La ficha debe ser verificada por el entrenador responsable."),
                LocalDateTime.now());
        return exportadorPdf.exportar(reporte);
    }

    private String nombreCliente(FichaEntrenamiento ficha) {
        if (ficha.getCliente() == null) {
            return "";
        }
        return (texto(ficha.getCliente().getNombre()) + " "
                + texto(ficha.getCliente().getApellido())).trim();
    }

    private String entrenadores(FichaEntrenamiento ficha) {
        if (ficha.getEntrenadores() == null || ficha.getEntrenadores().isEmpty()) {
            return "";
        }
        return ficha.getEntrenadores().stream()
                .map(Usuario::getUsuario)
                .filter(usuario -> usuario != null && !usuario.isBlank())
                .collect(Collectors.joining(", "));
    }

    private Map<String, Object> fila(String campo, Object detalle) {
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("campo", campo);
        fila.put("detalle", detalle == null ? "" : detalle);
        return fila;
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
