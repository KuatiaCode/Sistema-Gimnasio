package system_gym.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Catálogo de planes de membresía disponibles para la venta.
 * El precio se usa para calcular automáticamente el total de la membresía.
 */
@Entity
@Table(name = "tb_planes_membresia")
public class PlanesMembresia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Integer id;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "precio", nullable = false)
    private Long precio;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(name = "estado", nullable = false)
    private Short estado = 1; // 1: Activo, 0: Inactivo para ventas

    public PlanesMembresia() {}

    public PlanesMembresia(Integer id, String nombre, Long precio, String descripcion, Short estado) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getPrecio() {
        return precio;
    }

    public void setPrecio(Long precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Short getEstado() {
        return estado;
    }

    public void setEstado(Short estado) {
        this.estado = estado;
    }
}
