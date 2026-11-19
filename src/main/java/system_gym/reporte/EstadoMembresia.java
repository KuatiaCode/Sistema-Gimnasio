package system_gym.reporte;

/**
 * Estado efectivo de una membresía calculado con la fecha actual.
 */
public enum EstadoMembresia {
    ACTIVA("Activa"),
    VENCIDA("Vencida"),
    CONGELADA("Congelada"),
    INACTIVA("Inactiva");

    private final String texto;

    EstadoMembresia(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}
