package system_gym.reporte;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formatea valores de reportes para vistas HTML, CSV y PDF.
 */
public final class ReporteFormatoUtil {

    private static final Locale LOCALE = Locale.of("es", "PY");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter ISO_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ReporteFormatoUtil() {
    }

    public static String texto(Object valor, TipoDato tipo) {
        if (valor == null) {
            return "";
        }
        return switch (tipo) {
            case MONEDA -> "Gs. " + entero(valor);
            case DECIMAL -> decimal(valor);
            case ENTERO, LARGO -> entero(valor);
            case FECHA -> fecha(valor);
            case FECHA_HORA -> fechaHora(valor);
            case BOOLEANO -> booleano(valor);
            case TEXTO -> String.valueOf(valor);
        };
    }

    public static String csv(Object valor, TipoDato tipo) {
        if (valor == null) {
            return "";
        }
        return switch (tipo) {
            case MONEDA, ENTERO, LARGO -> numeroPlano(valor);
            case DECIMAL -> valor instanceof BigDecimal decimal
                    ? decimal.toPlainString()
                    : String.valueOf(valor);
            case FECHA -> valor instanceof LocalDate fecha ? fecha.format(ISO_FECHA)
                    : valor instanceof LocalDateTime fechaHora ? fechaHora.toLocalDate().format(ISO_FECHA)
                    : String.valueOf(valor);
            case FECHA_HORA -> valor instanceof LocalDateTime fechaHora ? fechaHora.format(ISO_FECHA_HORA)
                    : String.valueOf(valor);
            case BOOLEANO -> Boolean.TRUE.equals(valor) ? "SI" : "NO";
            case TEXTO -> String.valueOf(valor);
        };
    }

    public static String moneda(Long valor) {
        return valor == null ? "" : "Gs. " + entero(valor);
    }

    public static String entero(Object valor) {
        if (valor == null) {
            return "";
        }
        DecimalFormat formato = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(LOCALE));
        return formato.format(valor);
    }

    public static String decimal(Object valor) {
        if (valor == null) {
            return "";
        }
        BigDecimal numero = valor instanceof BigDecimal decimal
                ? decimal
                : new BigDecimal(String.valueOf(valor));
        DecimalFormat formato = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(LOCALE));
        return formato.format(numero.setScale(2, RoundingMode.HALF_UP));
    }

    public static String fecha(Object valor) {
        if (valor instanceof LocalDate fecha) {
            return fecha.format(FECHA);
        }
        if (valor instanceof LocalDateTime fechaHora) {
            return fechaHora.format(FECHA);
        }
        return valor == null ? "" : String.valueOf(valor);
    }

    public static String fechaHora(Object valor) {
        return valor instanceof LocalDateTime fechaHora ? fechaHora.format(FECHA_HORA)
                : valor == null ? "" : String.valueOf(valor);
    }

    public static String booleano(Object valor) {
        return Boolean.TRUE.equals(valor) ? "Sí" : "No";
    }

    private static String numeroPlano(Object valor) {
        if (valor instanceof BigDecimal decimal) {
            return decimal.toPlainString();
        }
        return String.valueOf(valor);
    }
}
