package system_gym.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_planes_membresia")
public class PlanesMembresia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Integer id;

    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
    private List<MembresiaCliente> usuariosMembresia = new ArrayList<>();

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    @Column(name = "precio", precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    @Column(name = "acceso_vip", nullable = false)
    private Boolean accesoVip = false;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "estado", nullable = false)
    private Short estado = 1; // 1: Activo, 0: Inactivo para ventas

    public PlanesMembresia() {}
    public PlanesMembresia(Integer id, String nombre, Integer duracionDias, BigDecimal precio, Boolean accesoVip, String descripcion, Short estado) {
        this.id = id;
        this.nombre = nombre;
        this.duracionDias = duracionDias;
        this.precio = precio;
        this.accesoVip = accesoVip;
        this.descripcion = descripcion;
        this.estado = estado;
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
    public Integer getDuracionDias() {
        return duracionDias;
    }
    public void setDuracionDias(Integer duracionDias) {
        this.duracionDias = duracionDias;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;   
    }
    public Boolean getAccesoVip() {
        return accesoVip;
    }
    public void setAccesoVip(Boolean accesoVip) {
        this.accesoVip = accesoVip;
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
