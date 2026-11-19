package system_gym.reporte;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Contenido completo de un reporte listo para previsualizar o exportar.
 */
public record ReporteData(
        String gimnasio,
        String titulo,
        String subtitulo,
        List<ReporteColumna> columnas,
        List<Map<String, Object>> filas,
        List<String> resumen,
        LocalDateTime generadoEn) {
}
