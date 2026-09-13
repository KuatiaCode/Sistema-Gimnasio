package system_gym.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
    @JoinColumn(name = "id_entrenador", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_fichas_entrenador"))
    private Usuario entrenador;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    @Column(name = "objetivo", length = 100)
    private String objetivo;

    @Column(name = "rutina_domingo", columnDefinition = "TEXT", nullable = false)
    private String rutinaDomingo;

    @Column(name = "rutina_lunes", columnDefinition = "TEXT", nullable = false)
    private String rutinaLunes;

    @Column(name = "rutina_martes", columnDefinition = "TEXT", nullable = false)
    private String rutinaMartes;

    @Column(name = "rutina_miercoles", columnDefinition = "TEXT", nullable = false)
    private String rutinaMiercoles;

    @Column(name = "rutina_jueves", columnDefinition = "TEXT", nullable = false)
    private String rutinaJueves;

    @Column(name = "rutina_viernes", columnDefinition = "TEXT", nullable = false)
    private String rutinaViernes;

    @Column(name = "rutina_sabado", columnDefinition = "TEXT", nullable = false)
    private String rutinaSabado;

    public FichaEntrenamiento() {}
    public FichaEntrenamiento(Cliente cliente, Usuario entrenador, String objetivo,
                              String rutinaLunes, String rutinaMartes, String rutinaMiercoles,
                              String rutinaJueves, String rutinaViernes, String rutinaSabado, String rutinaDomingo) {
        this.cliente = cliente;
        this.entrenador = entrenador;
        this.objetivo = objetivo;
        this.rutinaLunes = rutinaLunes;
        this.rutinaMartes = rutinaMartes;
        this.rutinaMiercoles = rutinaMiercoles;
        this.rutinaJueves = rutinaJueves;
        this.rutinaViernes = rutinaViernes;
        this.rutinaSabado = rutinaSabado;
        this.rutinaDomingo = rutinaDomingo;
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
    public Usuario getEntrenador() {
        return entrenador;
    }
    public void setEntrenador(Usuario entrenador) {
        this.entrenador = entrenador;
    }
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public String getObjetivo() {
        return objetivo;
    }
    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }
    public String getRutinaDomingo() {
        return rutinaDomingo;
    }
    public void setRutinaDomingo(String rutinaDomingo) {
        this.rutinaDomingo = rutinaDomingo;
    }
    public String getRutinaLunes() {
        return rutinaLunes;
    }
    public void setRutinaLunes(String rutinaLunes) {
        this.rutinaLunes = rutinaLunes;
    }
    public String getRutinaMartes() {
        return rutinaMartes;
    }
    public void setRutinaMartes(String rutinaMartes) {
        this.rutinaMartes = rutinaMartes;
    }
    public String getRutinaMiercoles() {
        return rutinaMiercoles;
    }
    public void setRutinaMiercoles(String rutinaMiercoles) {
        this.rutinaMiercoles = rutinaMiercoles;
    }
    public String getRutinaJueves() {
        return rutinaJueves;
    }
    public void setRutinaJueves(String rutinaJueves) {
        this.rutinaJueves = rutinaJueves;
    }
    public String getRutinaViernes() {
        return rutinaViernes;
    }
    public void setRutinaViernes(String rutinaViernes) {
        this.rutinaViernes = rutinaViernes;
    }
    public String getRutinaSabado() {
        return rutinaSabado;
    }
    public void setRutinaSabado(String rutinaSabado) {
        this.rutinaSabado = rutinaSabado;
    }

}
