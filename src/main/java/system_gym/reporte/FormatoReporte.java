package system_gym.reporte;

/**
 * Formatos de archivo disponibles para exportar reportes.
 */
public enum FormatoReporte {
    CSV("csv", "text/csv; charset=UTF-8"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PDF("pdf", "application/pdf");

    private final String extension;
    private final String tipoContenido;

    FormatoReporte(String extension, String tipoContenido) {
        this.extension = extension;
        this.tipoContenido = tipoContenido;
    }

    public String getExtension() {
        return extension;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public static FormatoReporte desde(String valor) {
        if (valor == null || valor.isBlank()) {
            return PDF;
        }
        return switch (valor.trim().toLowerCase()) {
            case "csv" -> CSV;
            case "xlsx", "excel" -> XLSX;
            case "pdf" -> PDF;
            default -> throw new IllegalArgumentException("Formato de reporte no válido: " + valor);
        };
    }
}
