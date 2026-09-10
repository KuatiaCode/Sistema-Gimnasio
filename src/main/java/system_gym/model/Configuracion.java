package system_gym.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tb_configuracion", uniqueConstraints = {
    @UniqueConstraint(name = "uq_configuracion_ruc", columnNames = "ruc")
})
public class Configuracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config")
    private Integer id;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "direccion", length = 255, nullable = false)
    private String direccion;

    @Column(name = "capacidad_maxima", nullable = false)
    private Short capacidadMaxima;

    @Column(name = "tiempo_sesion", nullable = false)
    private Short tiempoSesion = 30; // Minutos de inactividad para cierre automático de sesión

    @Column(name = "telefono", length = 45, nullable = false)
    private String telefono;

    @Column(name = "correo", length = 254, nullable = false)
    private String correo;

    @Column(name = "ruc", length = 20, nullable = false)
    private String ruc;

    public Configuracion() {}
    public Configuracion(Integer id, String nombre, String direccion, Short capacidadMaxima, Short tiempoSesion, String telefono, String correo, String ruc) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.capacidadMaxima = capacidadMaxima;
        this.tiempoSesion = tiempoSesion;
        this.telefono = telefono;
        this.correo = correo;
        this.ruc = ruc;
    }
    public Integer getid() {
        return id;
    }
    public void setid(Integer id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public Short getCapacidadMaxima() {
        return capacidadMaxima;
    }
    public void setCapacidadMaxima(Short capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }
    public Short getTiempoSesion() {
        return tiempoSesion;
    }
    public void setTiempoSesion(Short tiempoSesion) {
        this.tiempoSesion = tiempoSesion;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public String getRuc() {
        return ruc;
    }
    public void setRuc(String ruc) {
        this.ruc = ruc;
    }
    
}
