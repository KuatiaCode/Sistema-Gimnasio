package system_gym.service;

/**
 * Métricas del dashboard calculadas para el rol autenticado.
 */
public record DashboardResumen(
        long totalClientes,
        long membresiasActivas,
        long membresiasVencidas,
        long membresiasCongeladas,
        long pagosPendientesVencidos,
        long montoAdeudado,
        long ingresosMes,
        long fallasAbiertas,
        long evaluacionesMes,
        long entrenamientosMes,
        long clientesAtendidosMes) {
}
