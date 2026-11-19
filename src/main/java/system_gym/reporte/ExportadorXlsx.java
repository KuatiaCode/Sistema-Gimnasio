package system_gym.reporte;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 * Exporta reportes a un libro XLSX con encabezados, filtros y formatos numéricos.
 */
@Component
public class ExportadorXlsx implements ExportadorReporte {

    @Override
    public FormatoReporte formato() {
        return FormatoReporte.XLSX;
    }

    @Override
    public byte[] exportar(ReporteData reporte) {
        try (XSSFWorkbook libro = new XSSFWorkbook();
                ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Sheet hoja = libro.createSheet("Reporte");
            Estilos estilos = crearEstilos(libro);

            int ultimaColumna = Math.max(0, reporte.columnas().size() - 1);
            escribirTitulo(hoja, reporte, estilos, ultimaColumna);

            int filaResumen = 4;
            for (String linea : reporte.resumen()) {
                Row fila = hoja.createRow(filaResumen++);
                Cell celda = fila.createCell(0);
                celda.setCellValue(linea);
                celda.setCellStyle(estilos.resumen());
                if (ultimaColumna > 0) {
                    hoja.addMergedRegion(new CellRangeAddress(fila.getRowNum(), fila.getRowNum(), 0, ultimaColumna));
                }
            }

            int filaEncabezado = filaResumen + 1;
            escribirEncabezados(hoja, reporte.columnas(), filaEncabezado, estilos);
            escribirFilas(hoja, reporte, filaEncabezado + 1, estilos);

            for (int i = 0; i < reporte.columnas().size(); i++) {
                int ancho = Math.min(60, Math.max(10, reporte.columnas().get(i).ancho()));
                hoja.setColumnWidth(i, ancho * 256);
            }
            hoja.createFreezePane(0, filaEncabezado + 1);
            if (!reporte.filas().isEmpty()) {
                hoja.setAutoFilter(new CellRangeAddress(filaEncabezado, filaEncabezado + reporte.filas().size(),
                        0, ultimaColumna));
            }

            libro.write(salida);
            return salida.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el archivo XLSX.", e);
        }
    }

    private void escribirTitulo(Sheet hoja, ReporteData reporte, Estilos estilos, int ultimaColumna) {
        Row titulo = hoja.createRow(0);
        Cell celdaTitulo = titulo.createCell(0);
        celdaTitulo.setCellValue(reporte.gimnasio() + " - " + reporte.titulo());
        celdaTitulo.setCellStyle(estilos.titulo());
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, ultimaColumna));

        Row subtitulo = hoja.createRow(1);
        Cell celdaSubtitulo = subtitulo.createCell(0);
        celdaSubtitulo.setCellValue(reporte.subtitulo());
        celdaSubtitulo.setCellStyle(estilos.subtitulo());
        hoja.addMergedRegion(new CellRangeAddress(1, 1, 0, ultimaColumna));

        Row generado = hoja.createRow(2);
        Cell celdaGenerado = generado.createCell(0);
        celdaGenerado.setCellValue("Generado: " + ReporteFormatoUtil.fechaHora(reporte.generadoEn()));
        celdaGenerado.setCellStyle(estilos.subtitulo());
        hoja.addMergedRegion(new CellRangeAddress(2, 2, 0, ultimaColumna));
    }

    private void escribirEncabezados(Sheet hoja, List<ReporteColumna> columnas, int filaEncabezado, Estilos estilos) {
        Row fila = hoja.createRow(filaEncabezado);
        for (int i = 0; i < columnas.size(); i++) {
            Cell celda = fila.createCell(i);
            celda.setCellValue(columnas.get(i).titulo());
            celda.setCellStyle(estilos.encabezado());
        }
    }

    private void escribirFilas(Sheet hoja, ReporteData reporte, int primeraFila, Estilos estilos) {
        int numeroFila = primeraFila;
        for (Map<String, Object> datos : reporte.filas()) {
            Row fila = hoja.createRow(numeroFila++);
            for (int i = 0; i < reporte.columnas().size(); i++) {
                ReporteColumna columna = reporte.columnas().get(i);
                Cell celda = fila.createCell(i);
                escribirCelda(celda, datos.get(columna.clave()), columna.tipo(), estilos);
            }
        }
    }

    private void escribirCelda(Cell celda, Object valor, TipoDato tipo, Estilos estilos) {
        if (valor == null) {
            celda.setBlank();
            celda.setCellStyle(estilos.texto());
            return;
        }
        switch (tipo) {
            case MONEDA -> {
                celda.setCellValue(numero(valor));
                celda.setCellStyle(estilos.moneda());
            }
            case DECIMAL -> {
                celda.setCellValue(numero(valor));
                celda.setCellStyle(estilos.decimal());
            }
            case ENTERO, LARGO -> {
                celda.setCellValue(numero(valor));
                celda.setCellStyle(estilos.entero());
            }
            case FECHA -> {
                if (valor instanceof LocalDateTime fechaHora) {
                    celda.setCellValue(fechaHora.toLocalDate());
                } else if (valor instanceof LocalDate fecha) {
                    celda.setCellValue(fecha);
                } else {
                    celda.setCellValue(String.valueOf(valor));
                }
                celda.setCellStyle(estilos.fecha());
            }
            case FECHA_HORA -> {
                if (valor instanceof LocalDateTime fechaHora) {
                    celda.setCellValue(fechaHora);
                } else {
                    celda.setCellValue(String.valueOf(valor));
                }
                celda.setCellStyle(estilos.fechaHora());
            }
            case BOOLEANO -> {
                celda.setCellValue(Boolean.TRUE.equals(valor));
                celda.setCellStyle(estilos.booleano());
            }
            case TEXTO -> {
                celda.setCellValue(String.valueOf(valor));
                celda.setCellStyle(estilos.texto());
            }
        }
    }

    private double numero(Object valor) {
        if (valor instanceof BigDecimal decimal) {
            return decimal.doubleValue();
        }
        if (valor instanceof Number numero) {
            return numero.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(valor));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Estilos crearEstilos(XSSFWorkbook libro) {
        var formatos = libro.createDataFormat();

        Font fuenteTitulo = libro.createFont();
        fuenteTitulo.setBold(true);
        fuenteTitulo.setFontHeightInPoints((short) 16);

        Font fuenteSubtitulo = libro.createFont();
        fuenteSubtitulo.setItalic(true);
        fuenteSubtitulo.setColor(IndexedColors.GREY_50_PERCENT.getIndex());

        Font fuenteEncabezado = libro.createFont();
        fuenteEncabezado.setBold(true);
        fuenteEncabezado.setColor(IndexedColors.WHITE.getIndex());

        CellStyle titulo = libro.createCellStyle();
        titulo.setFont(fuenteTitulo);

        CellStyle subtitulo = libro.createCellStyle();
        subtitulo.setFont(fuenteSubtitulo);

        CellStyle resumen = libro.createCellStyle();
        resumen.setFont(fuenteEncabezado);
        resumen.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        resumen.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle encabezado = libro.createCellStyle();
        encabezado.setFont(fuenteEncabezado);
        encabezado.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        encabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        encabezado.setAlignment(HorizontalAlignment.CENTER);
        encabezado.setVerticalAlignment(VerticalAlignment.CENTER);
        encabezado.setBorderBottom(BorderStyle.THIN);

        CellStyle texto = libro.createCellStyle();
        texto.setWrapText(true);
        texto.setVerticalAlignment(VerticalAlignment.TOP);
        texto.setBorderBottom(BorderStyle.HAIR);

        CellStyle fecha = libro.createCellStyle();
        fecha.cloneStyleFrom(texto);
        fecha.setDataFormat(formatos.getFormat("dd/mm/yyyy"));
        fecha.setAlignment(HorizontalAlignment.CENTER);

        CellStyle fechaHora = libro.createCellStyle();
        fechaHora.cloneStyleFrom(texto);
        fechaHora.setDataFormat(formatos.getFormat("dd/mm/yyyy hh:mm"));
        fechaHora.setAlignment(HorizontalAlignment.CENTER);

        CellStyle entero = libro.createCellStyle();
        entero.cloneStyleFrom(texto);
        entero.setDataFormat(formatos.getFormat("#,##0"));
        entero.setAlignment(HorizontalAlignment.RIGHT);

        CellStyle decimal = libro.createCellStyle();
        decimal.cloneStyleFrom(texto);
        decimal.setDataFormat(formatos.getFormat("#,##0.00"));
        decimal.setAlignment(HorizontalAlignment.RIGHT);

        CellStyle moneda = libro.createCellStyle();
        moneda.cloneStyleFrom(texto);
        moneda.setDataFormat(formatos.getFormat("#,##0"));
        moneda.setAlignment(HorizontalAlignment.RIGHT);

        CellStyle booleano = libro.createCellStyle();
        booleano.cloneStyleFrom(texto);
        booleano.setAlignment(HorizontalAlignment.CENTER);

        return new Estilos(titulo, subtitulo, resumen, encabezado, texto, fecha, fechaHora, entero, decimal,
                moneda, booleano);
    }

    private record Estilos(CellStyle titulo, CellStyle subtitulo, CellStyle resumen, CellStyle encabezado,
            CellStyle texto, CellStyle fecha, CellStyle fechaHora, CellStyle entero, CellStyle decimal,
            CellStyle moneda, CellStyle booleano) {
    }
}
