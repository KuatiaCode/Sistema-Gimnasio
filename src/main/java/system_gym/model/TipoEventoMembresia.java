package system_gym.model;

public enum TipoEventoMembresia {
    RENOVACION("Renovación", "renovacion"),
    CANCELACION("Cancelación", "cancelacion"),
    CONGELACION("Congelación", "congelacion"),
    REACTIVACION("Reactivación", "reactivacion");

    private final String texto;
    private final String slug;

    TipoEventoMembresia(String texto, String slug) {
        this.texto = texto;
        this.slug = slug;
    }

    public String getTexto() {
        return texto;
    }

    public String getSlug() {
        return slug;
    }
}
