package system_gym.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Falla técnica reportada sobre equipos o instalaciones del gimnasio.
 */
@Entity
@Table(name = "tb_falla", indexes = {
    @Index(name = "idx_falla_estado", columnList = "estado"),
    @Index(name = "idx_falla_fecha_reporte", columnList = "fecha_reporte"),
    @Index(name = "idx_falla_responsable", columnList = "id_responsable_atencion")
})
public class Falla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_falla")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_reporta", nullable = false,
                foreignKey = @ForeignKey(name = "fk_falla_usuario_reporta"))
    private Usuario usuarioReporta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsable_atencion",
                foreignKey = @ForeignKey(name = "fk_falla_responsable"))
    private Usuario responsableAtencion;

    @Column(name = "reportado_por", length = 100, nullable = false)
    private String reportadoPor;

    @NotBlank(message = "{falla.area.obligatoria}")
    @Size(max = 100, message = "{falla.area.longitud}")
    @Column(name = "area_equipo", length = 100)
    private String areaEquipo;

    @NotBlank(message = "{falla.descripcion.obligatoria}")
    @Size(min = 10, max = 1000, message = "{falla.descripcion.longitud}")
    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @NotNull(message = "{falla.prioridad.obligatoria}")
    @Min(value = 1, message = "{falla.prioridad.rango}")
    @Max(value = 3, message = "{falla.prioridad.rango}")
    @Column(name = "prioridad", nullable = false)
    private Short prioridad = 2; // 1: Baja, 2: Media, 3: Alta

    @NotNull(message = "{falla.estado.rango}")
    @Min(value = 1, message = "{falla.estado.rango}")
    @Max(value = 3, message = "{falla.estado.rango}")
    @Column(name = "estado", nullable = false)
    private Short estado = 1; // 1: Abierta, 2: En proceso, 3: Resuelta

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte = LocalDateTime.now();

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Size(max = 1000, message = "{falla.observacion.longitud}")
    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    public Falla() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuarioReporta() {
        return usuarioReporta;
    }

    public void setUsuarioReporta(Usuario usuarioReporta) {
        this.usuarioReporta = usuarioReporta;
    }

    public Usuario getResponsableAtencion() {
        return responsableAtencion;
    }

    public void setResponsableAtencion(Usuario responsableAtencion) {
        this.responsableAtencion = responsableAtencion;
    }

    public String getReportadoPor() {
        return reportadoPor;
    }

    public void setReportadoPor(String reportadoPor) {
        this.reportadoPor = reportadoPor;
    }

    public String getAreaEquipo() {
        return areaEquipo;
    }

    public void setAreaEquipo(String areaEquipo) {
        this.areaEquipo = areaEquipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Short getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Short prioridad) {
        this.prioridad = prioridad;
    }

    public Short getEstado() {
        return estado;
    }

    public void setEstado(Short estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(LocalDateTime fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getEstadoTexto() {
        return switch (estado == null ? 0 : estado) {
            case 1 -> "Abierta";
            case 2 -> "En proceso";
            case 3 -> "Resuelta";
            default -> "";
        };
    }

    public String getPrioridadTexto() {
        return switch (prioridad == null ? 0 : prioridad) {
            case 1 -> "Baja";
            case 2 -> "Media";
            case 3 -> "Alta";
            default -> "";
        };
    }
}
