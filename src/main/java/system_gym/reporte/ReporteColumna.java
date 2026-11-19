package system_gym.reporte;

/**
 * Definición de una columna de reporte.
 */
public record ReporteColumna(String clave, String titulo, TipoDato tipo, int ancho) {

    public ReporteColumna(String clave, String titulo, TipoDato tipo) {
        this(clave, titulo, tipo, 16);
    }
}
