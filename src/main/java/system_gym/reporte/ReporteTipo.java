package system_gym.reporte;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Catálogo de reportes del sistema y los roles autorizados a consultarlos.
 */
public enum ReporteTipo {
    MIEMBROS("miembros", "Reporte de miembros", true, true, false),
    MEMBRESIAS("membresias", "Reporte de membresías", true, true, false),
    INGRESOS("ingresos", "Reporte de ingresos", true, true, false),
    MOROSIDAD("morosidad", "Reporte de morosidad", true, true, false),
    ENTRENAMIENTOS("entrenamientos", "Reporte de entrenamientos", true, false, true),
    EVALUACIONES("evaluaciones", "Reporte de evaluaciones", true, false, true),
    COMPARATIVA("comparativa", "Comparativa de evaluaciones", true, false, true),
    FALLAS("fallas", "Reporte de fallas", true, true, true),
    AUDITORIA("auditoria", "Reporte de auditoría", true, false, false);

    private final String slug;
    private final String titulo;
    private final boolean admin;
    private final boolean recepcionista;
    private final boolean entrenador;

    ReporteTipo(String slug, String titulo, boolean admin, boolean recepcionista, boolean entrenador) {
        this.slug = slug;
        this.titulo = titulo;
        this.admin = admin;
        this.recepcionista = recepcionista;
        this.entrenador = entrenador;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean puedeVer(boolean esAdmin, boolean esRecepcionista, boolean esEntrenador) {
        return (esAdmin && admin) || (esRecepcionista && recepcionista) || (esEntrenador && entrenador);
    }

    public static Optional<ReporteTipo> desdeSlug(String slug) {
        if (slug == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(tipo -> tipo.slug.equalsIgnoreCase(slug))
                .findFirst();
    }

    public static List<ReporteTipo> disponibles(boolean esAdmin, boolean esRecepcionista, boolean esEntrenador) {
        return Arrays.stream(values())
                .filter(tipo -> tipo.puedeVer(esAdmin, esRecepcionista, esEntrenador))
                .toList();
    }
}
