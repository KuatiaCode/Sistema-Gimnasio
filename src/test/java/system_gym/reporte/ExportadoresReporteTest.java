package system_gym.reporte;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import system_gym.model.MembresiaCliente;

class ExportadoresReporteTest {

    private final ReporteData reporte = new ReporteData(
            "Oficina del Cuerpo",
            "Reporte de prueba",
            "Sin filtros",
            List.of(
                    new ReporteColumna("id", "ID", TipoDato.ENTERO),
                    new ReporteColumna("nombre", "Nombre", TipoDato.TEXTO),
                    new ReporteColumna("fecha", "Fecha", TipoDato.FECHA),
                    new ReporteColumna("monto", "Monto", TipoDato.MONEDA)),
            List.of(fila()),
            List.of("Total: 1"),
            LocalDateTime.of(2026, 11, 18, 20, 30));

    @Test
    void csvIncluyeEncabezadosYValores() {
        String contenido = new String(new ExportadorCsv().exportar(reporte), StandardCharsets.UTF_8);
        assertTrue(contenido.contains("ID;Nombre;Fecha;Monto"));
        assertTrue(contenido.contains("1;Ana Pérez;2026-11-18;1500000"));
    }

    @Test
    void xlsxGeneraUnArchivoZipValido() {
        byte[] contenido = new ExportadorXlsx().exportar(reporte);
        assertEquals('P', contenido[0]);
        assertEquals('K', contenido[1]);
        assertTrue(contenido.length > 100);
    }

    @Test
    void pdfGeneraUnDocumentoValido() {
        byte[] contenido = new ExportadorPdf().exportar(reporte);
        assertEquals("%PDF", new String(contenido, 0, 4, StandardCharsets.US_ASCII));
        assertTrue(contenido.length > 100);
    }

    @Test
    void formatoMonedaUsaSeparadorDeMiles() {
        assertEquals("Gs. 1.500.000", ReporteFormatoUtil.moneda(1_500_000L));
    }

    @Test
    void estadoMembresiaSeCalculaConLaFechaActual() {
        LocalDate hoy = LocalDate.of(2026, 11, 18);
        MembresiaCliente activa = membresia((short) 1, hoy.plusDays(1));
        MembresiaCliente vencida = membresia((short) 1, hoy.minusDays(1));
        MembresiaCliente congelada = membresia((short) 3, hoy.plusDays(10));

        assertEquals(EstadoMembresia.ACTIVA, EstadoMembresiaUtil.calcular(activa, hoy));
        assertEquals(EstadoMembresia.VENCIDA, EstadoMembresiaUtil.calcular(vencida, hoy));
        assertEquals(EstadoMembresia.CONGELADA, EstadoMembresiaUtil.calcular(congelada, hoy));
    }

    private Map<String, Object> fila() {
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("id", 1);
        fila.put("nombre", "Ana Pérez");
        fila.put("fecha", LocalDate.of(2026, 11, 18));
        fila.put("monto", 1_500_000L);
        return fila;
    }

    private MembresiaCliente membresia(Short estado, LocalDate vencimiento) {
        MembresiaCliente membresia = new MembresiaCliente();
        membresia.setEstado(estado);
        membresia.setFechaVencimiento(vencimiento);
        return membresia;
    }
}
