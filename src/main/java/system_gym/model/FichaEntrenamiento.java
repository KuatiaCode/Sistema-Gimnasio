package system_gym.model;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

/**
 * Plan de entrenamiento semanal asignado a un cliente.
 * Se relaciona con la membresía del cliente, sus planes, sus días de asistencia
 * y los entrenadores responsables.
 */
@Entity
@Table(name = "tb_ficha_entrenamiento")
public class FichaEntrenamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ficha")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false,
                foreignKey = @ForeignKey(name = "fk_fichas_cliente"))
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_membresia", nullable = false,
                foreignKey = @ForeignKey(name = "fk_fichas_membresia"))
    private MembresiaCliente membresia;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tb_ficha_entrenadores",
            joinColumns = @JoinColumn(name = "id_ficha"),
            inverseJoinColumns = @JoinColumn(name = "id_usuario"),
            uniqueConstraints = @UniqueConstraint(name = "uq_ficha_entrenador",
                    columnNames = {"id_ficha", "id_usuario"}))
    private Set<Usuario> entrenadores = new LinkedHashSet<>();

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "objetivo", length = 100)
    private String objetivo;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tb_ficha_dias",
            joinColumns = @JoinColumn(name = "id_ficha"),
            uniqueConstraints = @UniqueConstraint(name = "uq_ficha_dia",
                    columnNames = {"id_ficha", "dia_semana"}))
    @Column(name = "dia_semana", nullable = false)
    private Set<Short> diasEntrenamiento = new LinkedHashSet<>();

    public FichaEntrenamiento() {}

    public FichaEntrenamiento(Cliente cliente, MembresiaCliente membresia, Set<Usuario> entrenadores,
            LocalDateTime fechaCreacion, String objetivo, Set<Short> diasEntrenamiento) {
        this.cliente = cliente;
        this.membresia = membresia;
        this.entrenadores = entrenadores;
        this.fechaCreacion = fechaCreacion;
        this.objetivo = objetivo;
        this.diasEntrenamiento = diasEntrenamiento;
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

    public MembresiaCliente getMembresia() {
        return membresia;
    }

    public void setMembresia(MembresiaCliente membresia) {
        this.membresia = membresia;
    }

    public Set<Usuario> getEntrenadores() {
        return entrenadores;
    }

    public void setEntrenadores(Set<Usuario> entrenadores) {
        this.entrenadores = entrenadores;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public Set<Short> getDiasEntrenamiento() {
        return diasEntrenamiento;
    }

    public void setDiasEntrenamiento(Set<Short> diasEntrenamiento) {
        this.diasEntrenamiento = diasEntrenamiento;
    }

    @Transient
    public String getDiasSemanaTexto() {
        if (diasEntrenamiento == null || diasEntrenamiento.isEmpty()) {
            return "";
        }
        String[] nombres = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        return diasEntrenamiento.stream()
                .filter(dia -> dia != null && dia >= 1 && dia <= 7)
                .sorted()
                .map(dia -> nombres[dia - 1])
                .collect(Collectors.joining(", "));
    }
}
