package system_gym.reporte;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

/**
 * Exporta reportes separados por punto y coma, codificados en UTF-8 con BOM.
 */
@Component
public class ExportadorCsv implements ExportadorReporte {

    @Override
    public FormatoReporte formato() {
        return FormatoReporte.CSV;
    }

    @Override
    public byte[] exportar(ReporteData reporte) {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            salida.write(0xEF);
            salida.write(0xBB);
            salida.write(0xBF);

            try (Writer escritor = new OutputStreamWriter(salida, StandardCharsets.UTF_8);
                    CSVPrinter impresora = new CSVPrinter(escritor, formatoCsv(reporte))) {
                for (Map<String, Object> fila : reporte.filas()) {
                    List<String> valores = reporte.columnas().stream()
                            .map(columna -> ReporteFormatoUtil.csv(
                                    fila.get(columna.clave()), columna.tipo()))
                            .toList();
                    impresora.printRecord(valores);
                }
            }
            return salida.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el archivo CSV.", e);
        }
    }

    private CSVFormat formatoCsv(ReporteData reporte) {
        String[] encabezados = reporte.columnas().stream()
                .map(ReporteColumna::titulo)
                .toArray(String[]::new);
        return CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setHeader(encabezados)
                .setRecordSeparator("\r\n")
                .build();
    }
}
