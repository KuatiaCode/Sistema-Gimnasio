package system_gym.reporte;

import java.time.LocalDate;

import system_gym.model.MembresiaCliente;

/**
 * Calcula el estado efectivo de una membresía sin modificar la base de datos.
 */
public final class EstadoMembresiaUtil {

    private EstadoMembresiaUtil() {
    }

    public static EstadoMembresia calcular(MembresiaCliente membresia, LocalDate hoy) {
        if (membresia == null) {
            return EstadoMembresia.INACTIVA;
        }
        if (membresia.getEstado() != null && membresia.getEstado() == 4) {
            return EstadoMembresia.INACTIVA;
        }
        if (membresia.getEstado() != null && membresia.getEstado() == 3) {
            return EstadoMembresia.CONGELADA;
        }
        if (membresia.getFechaVencimiento() != null
                && membresia.getFechaVencimiento().isBefore(hoy)) {
            return EstadoMembresia.VENCIDA;
        }
        if (membresia.getEstado() != null && membresia.getEstado() == 1) {
            return EstadoMembresia.ACTIVA;
        }
        if (membresia.getEstado() != null && membresia.getEstado() == 2) {
            return EstadoMembresia.VENCIDA;
        }
        return EstadoMembresia.INACTIVA;
    }

    public static String texto(MembresiaCliente membresia, LocalDate hoy) {
        return calcular(membresia, hoy).getTexto();
    }
}
