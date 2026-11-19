package system_gym.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_membresias_eventos", indexes = {
    @Index(name = "idx_membresia_evento_membresia", columnList = "id_membresia"),
    @Index(name = "idx_membresia_evento_fecha", columnList = "fecha_evento")
})
public class MembresiaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_membresia", nullable = false,
                foreignKey = @ForeignKey(name = "fk_membresia_evento_membresia"))
    private MembresiaCliente membresia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20, nullable = false)
    private TipoEventoMembresia tipo;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento = LocalDateTime.now();

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_vencimiento_anterior")
    private LocalDate fechaVencimientoAnterior;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_vencimiento_nueva")
    private LocalDate fechaVencimientoNueva;

    @Column(name = "dias")
    private Integer dias;

    @Column(name = "monto")
    private Long monto;

    @Column(name = "responsable", length = 100)
    private String responsable;

    public MembresiaEvento() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MembresiaCliente getMembresia() {
        return membresia;
    }

    public void setMembresia(MembresiaCliente membresia) {
        this.membresia = membresia;
    }

    public TipoEventoMembresia getTipo() {
        return tipo;
    }

    public void setTipo(TipoEventoMembresia tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDate getFechaVencimientoAnterior() {
        return fechaVencimientoAnterior;
    }

    public void setFechaVencimientoAnterior(LocalDate fechaVencimientoAnterior) {
        this.fechaVencimientoAnterior = fechaVencimientoAnterior;
    }

    public LocalDate getFechaVencimientoNueva() {
        return fechaVencimientoNueva;
    }

    public void setFechaVencimientoNueva(LocalDate fechaVencimientoNueva) {
        this.fechaVencimientoNueva = fechaVencimientoNueva;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public Long getMonto() {
        return monto;
    }

    public void setMonto(Long monto) {
        this.monto = monto;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
}
