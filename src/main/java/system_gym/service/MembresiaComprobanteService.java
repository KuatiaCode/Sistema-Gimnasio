package system_gym.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.MembresiaCliente;
import system_gym.model.MembresiaEvento;
import system_gym.reporte.ExportadorPdf;
import system_gym.reporte.ReporteColumna;
import system_gym.reporte.ReporteData;
import system_gym.reporte.ReporteFormatoUtil;
import system_gym.reporte.TipoDato;
import system_gym.repository.MembresiaClienteRepository;
import system_gym.repository.MembresiaEventoRepository;

@Service
public class MembresiaComprobanteService {

    private final MembresiaClienteRepository membresiaClienteRepository;
    private final MembresiaEventoRepository membresiaEventoRepository;
    private final ConfiguracionService configuracionService;
    private final ExportadorPdf exportadorPdf;

    public MembresiaComprobanteService(MembresiaClienteRepository membresiaClienteRepository,
            MembresiaEventoRepository membresiaEventoRepository,
            ConfiguracionService configuracionService, ExportadorPdf exportadorPdf) {
        this.membresiaClienteRepository = membresiaClienteRepository;
        this.membresiaEventoRepository = membresiaEventoRepository;
        this.configuracionService = configuracionService;
        this.exportadorPdf = exportadorPdf;
    }

    @Transactional(readOnly = true)
    public byte[] generar(Integer membresiaId, Integer eventoId) {
        MembresiaCliente membresia = membresiaClienteRepository.findById(membresiaId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada con id " + membresiaId));
        MembresiaEvento evento = membresiaEventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado con id " + eventoId));
        if (evento.getMembresia() == null || !membresiaId.equals(evento.getMembresia().getId())) {
            throw new IllegalArgumentException("El comprobante no pertenece a la membresía indicada.");
        }

        String gimnasio = configuracionService.obtenerConfiguracion().getNombre();
        if (gimnasio == null || gimnasio.isBlank()) {
            gimnasio = "Sistema de gestión";
        }
        String cliente = membresia.getCliente() == null
                ? ""
                : membresia.getCliente().getNombre() + " " + membresia.getCliente().getApellido();
        String documento = membresia.getCliente() == null ? "" : membresia.getCliente().getDocumento();
        String planes = membresia.getPlanes() == null
                ? ""
                : membresia.getPlanes().stream()
                        .map(plan -> plan.getNombre())
                        .collect(Collectors.joining(", "));

        List<Map<String, Object>> filas = new ArrayList<>();
        filas.add(fila("Comprobante", "#" + evento.getId()));
        filas.add(fila("Cliente", cliente));
        filas.add(fila("Documento", documento));
        filas.add(fila("Membresía", "#" + membresia.getId()));
        filas.add(fila("Planes", planes));
        filas.add(fila("Operación", evento.getTipo() == null ? "" : evento.getTipo().getTexto()));
        filas.add(fila("Fecha de operación", ReporteFormatoUtil.fechaHora(evento.getFechaEvento())));
        filas.add(fila("Inicio del período", ReporteFormatoUtil.fecha(evento.getFechaInicio())));
        filas.add(fila("Fin del período", ReporteFormatoUtil.fecha(evento.getFechaFin())));
        filas.add(fila("Vencimiento anterior",
                ReporteFormatoUtil.fecha(evento.getFechaVencimientoAnterior())));
        filas.add(fila("Vencimiento nuevo",
                ReporteFormatoUtil.fecha(evento.getFechaVencimientoNueva())));
        filas.add(fila("Días ajustados", ReporteFormatoUtil.entero(evento.getDias())));
        filas.add(fila("Monto de referencia", ReporteFormatoUtil.moneda(evento.getMonto())));
        filas.add(fila("Motivo", evento.getMotivo()));
        filas.add(fila("Responsable", evento.getResponsable()));

        List<ReporteColumna> columnas = List.of(
                new ReporteColumna("campo", "Campo", TipoDato.TEXTO, 30),
                new ReporteColumna("detalle", "Detalle", TipoDato.TEXTO, 90));
        ReporteData reporte = new ReporteData(gimnasio,
                "Comprobante de " + (evento.getTipo() == null ? "operación" : evento.getTipo().getTexto()),
                "Membresía #" + membresia.getId() + " - " + cliente,
                columnas, filas,
                List.of("Documento generado automáticamente por el sistema."),
                LocalDateTime.now());
        return exportadorPdf.exportar(reporte);
    }

    private Map<String, Object> fila(String campo, Object detalle) {
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("campo", campo);
        fila.put("detalle", detalle == null ? "" : detalle);
        return fila;
    }
}
