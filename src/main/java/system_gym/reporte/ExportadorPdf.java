package system_gym.reporte;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Exporta reportes a PDF horizontal con encabezado, resumen y numeración de páginas.
 */
@Component
public class ExportadorPdf implements ExportadorReporte {

    private static final Font FUENTE_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font FUENTE_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.ITALIC);
    private static final Font FUENTE_ENCABEZADO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8,
            Font.NORMAL, Color.WHITE);
    private static final Font FUENTE_DATO = FontFactory.getFont(FontFactory.HELVETICA, 8);
    private static final Font FUENTE_PIE = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL,
            new Color(100, 100, 100));

    @Override
    public FormatoReporte formato() {
        return FormatoReporte.PDF;
    }

    @Override
    public byte[] exportar(ReporteData reporte) {
        Document documento = new Document(PageSize.A4.rotate(), 24, 24, 24, 32);
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            PdfWriter escritor = PdfWriter.getInstance(documento, salida);
            escritor.setPageEvent(new PiePagina());
            documento.open();

            documento.add(new Paragraph(reporte.gimnasio(), FUENTE_SUBTITULO));
            documento.add(new Paragraph(reporte.titulo(), FUENTE_TITULO));
            documento.add(new Paragraph(reporte.subtitulo(), FUENTE_SUBTITULO));
            documento.add(new Paragraph("Generado: " + ReporteFormatoUtil.fechaHora(reporte.generadoEn()),
                    FUENTE_SUBTITULO));
            documento.add(new Paragraph(" "));

            for (String linea : reporte.resumen()) {
                Paragraph parrafo = new Paragraph("• " + linea, FUENTE_SUBTITULO);
                parrafo.setSpacingAfter(1);
                documento.add(parrafo);
            }

            if (!reporte.resumen().isEmpty()) {
                documento.add(new Paragraph(" "));
            }

            if (reporte.filas().isEmpty()) {
                documento.add(new Paragraph("No hay datos para los filtros seleccionados.", FUENTE_DATO));
            } else {
                documento.add(crearTabla(reporte));
            }

            documento.close();
            return salida.toByteArray();
        } catch (Exception e) {
            if (documento.isOpen()) {
                documento.close();
            }
            throw new IllegalStateException("No se pudo generar el archivo PDF.", e);
        }
    }

    private PdfPTable crearTabla(ReporteData reporte) {
        List<ReporteColumna> columnas = reporte.columnas();
        PdfPTable tabla = new PdfPTable(columnas.size());
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);
        tabla.setWidths(anchos(columnas));

        for (ReporteColumna columna : columnas) {
            PdfPCell celda = new PdfPCell(new Phrase(columna.titulo(), FUENTE_ENCABEZADO));
            celda.setBackgroundColor(new Color(31, 78, 121));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setPadding(4);
            tabla.addCell(celda);
        }

        for (Map<String, Object> fila : reporte.filas()) {
            for (ReporteColumna columna : columnas) {
                String valor = ReporteFormatoUtil.texto(fila.get(columna.clave()), columna.tipo());
                PdfPCell celda = new PdfPCell(new Phrase(valor, FUENTE_DATO));
                celda.setPadding(3);
                celda.setBorderColor(new Color(210, 210, 210));
                celda.setHorizontalAlignment(alineacion(columna.tipo()));
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                tabla.addCell(celda);
            }
        }
        return tabla;
    }

    private float[] anchos(List<ReporteColumna> columnas) {
        float[] anchos = new float[columnas.size()];
        for (int i = 0; i < columnas.size(); i++) {
            anchos[i] = Math.max(8, columnas.get(i).ancho());
        }
        return anchos;
    }

    private int alineacion(TipoDato tipo) {
        return switch (tipo) {
            case MONEDA, ENTERO, LARGO, DECIMAL -> Element.ALIGN_RIGHT;
            case FECHA, FECHA_HORA, BOOLEANO -> Element.ALIGN_CENTER;
            case TEXTO -> Element.ALIGN_LEFT;
        };
    }

    private static class PiePagina extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase("Página " + writer.getPageNumber(), FUENTE_PIE),
                    (document.left() + document.right()) / 2, document.bottom() - 10, 0);
        }
    }
}
