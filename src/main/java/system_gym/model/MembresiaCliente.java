package system_gym.model;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Membresía contratada por un cliente para uno o varios planes.
 * Guarda la vigencia, el estado y el total acordado en el momento de la compra.
 */
@Entity
@Table(name = "tb_membresias_clientes")
public class MembresiaCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_membresia")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false,
                foreignKey = @ForeignKey(name = "fk_membresias_cliente"))
    private Cliente cliente;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tb_membresias_planes",
            joinColumns = @JoinColumn(name = "id_membresia"),
            inverseJoinColumns = @JoinColumn(name = "id_plan"),
            uniqueConstraints = @UniqueConstraint(name = "uq_membresia_plan",
                    columnNames = {"id_membresia", "id_plan"}))
    private Set<PlanesMembresia> planes = new LinkedHashSet<>();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "estado", nullable = false)
    private Short estado; // 1: Activa, 2: Vencida, 3: Congelada, 4: Inactiva/Cancelada

    // Total histórico calculado con los precios de los planes al crear la membresía.
    @Column(name = "precio_a_pagar", nullable = false)
    private Long precioAPagar = 0L;

    @Column(name = "renovacion_automatica", nullable = false)
    private Boolean renovacionAutomatica = false;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_cancelacion")
    private LocalDate fechaCancelacion;

    @Column(name = "motivo_cancelacion", columnDefinition = "TEXT")
    private String motivoCancelacion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_congelacion_inicio")
    private LocalDate fechaCongelacionInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_congelacion_fin")
    private LocalDate fechaCongelacionFin;

    @Column(name = "motivo_congelacion", columnDefinition = "TEXT")
    private String motivoCongelacion;

    @Column(name = "dias_congelados")
    private Integer diasCongelados = 0;

    public MembresiaCliente() {}

    public MembresiaCliente(Integer id, Cliente cliente, Set<PlanesMembresia> planes,
            LocalDate fechaInicio, LocalDate fechaVencimiento, Short estado, Long precioAPagar,
            Boolean renovacionAutomatica, String observaciones) {
        this.id = id;
        this.cliente = cliente;
        this.planes = planes;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
        this.precioAPagar = precioAPagar;
        this.renovacionAutomatica = renovacionAutomatica;
        this.observaciones = observaciones;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Set<PlanesMembresia> getPlanes() {
        return planes;
    }

    public void setPlanes(Set<PlanesMembresia> planes) {
        this.planes = planes;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Short getEstado() {
        return estado;
    }

    public void setEstado(Short estado) {
        this.estado = estado;
    }

    public Long getPrecioAPagar() {
        return precioAPagar;
    }

    public void setPrecioAPagar(Long precioAPagar) {
        this.precioAPagar = precioAPagar;
    }

    public Boolean getRenovacionAutomatica() {
        return renovacionAutomatica;
    }

    public void setRenovacionAutomatica(Boolean renovacionAutomatica) {
        this.renovacionAutomatica = renovacionAutomatica;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDate getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(LocalDate fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public LocalDate getFechaCongelacionInicio() {
        return fechaCongelacionInicio;
    }

    public void setFechaCongelacionInicio(LocalDate fechaCongelacionInicio) {
        this.fechaCongelacionInicio = fechaCongelacionInicio;
    }

    public LocalDate getFechaCongelacionFin() {
        return fechaCongelacionFin;
    }

    public void setFechaCongelacionFin(LocalDate fechaCongelacionFin) {
        this.fechaCongelacionFin = fechaCongelacionFin;
    }

    public String getMotivoCongelacion() {
        return motivoCongelacion;
    }

    public void setMotivoCongelacion(String motivoCongelacion) {
        this.motivoCongelacion = motivoCongelacion;
    }

    public Integer getDiasCongelados() {
        return diasCongelados;
    }

    public void setDiasCongelados(Integer diasCongelados) {
        this.diasCongelados = diasCongelados;
    }

    public String getEstadoTexto() {
        return switch (estado == null ? 0 : estado) {
            case 1 -> "Activa";
            case 2 -> "Vencida";
            case 3 -> "Congelada";
            case 4 -> "Inactiva";
            default -> "";
        };
    }
}
