package system_gym.reporte;

/**
 * Contrato común para exportar un reporte a un formato de archivo.
 */
public interface ExportadorReporte {

    FormatoReporte formato();

    byte[] exportar(ReporteData reporte);
}
