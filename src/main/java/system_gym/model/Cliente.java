package system_gym.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tb_cliente", uniqueConstraints = {
    @UniqueConstraint(name = "uq_cliente_documento", columnNames = "documento"),
    @UniqueConstraint(name = "uq_cliente_correo", columnNames = "correo")
})
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer id;

    @OneToMany (mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY) 
    private List<MembresiaCliente> membresias = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY) 
    private List<Pago> pagos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FichaMedica> fichasMedicas = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FichaEntrenamiento> fichasEntrenamiento = new ArrayList<>();

    @Column(name = "nombre", length = 45, nullable = false)
    private String nombre;

    @Column(name = "apellido", length = 45, nullable = false)
    private String apellido;

    @Column(name = "telefono", length = 20, nullable = false)
    private String telefono;

    @Column(name = "documento", length = 15, nullable = false)
    private String documento;

    @Column(name = "correo", length = 254, nullable = false)
    private String correo;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro = LocalDate.now();

    @Column(name = "sexo", nullable = false)
    private Short sexo; // 0: Masculino, 1: Femenino, u otros códigos paramétricos

    @Column(name = "direccion", length = 255, nullable = false)
    private String direccion;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "contacto_emergencia", length = 45)
    private String contactoEmergencia;

    @Column(name = "telefono_emergencia", length = 20)
    private String telefonoEmergencia;

    @Column(name = "ocupacion", length = 100)
    private String ocupacion;

    @Column(name = "fotografia", length = 255)
    private String fotografia;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    public Cliente() {}
    public Cliente(Integer idCliente, String nombre, String apellido, String telefono, String documento, String correo, LocalDate fechaRegistro, Short sexo, String direccion, String ciudad, String contactoEmergencia, String telefonoEmergencia, String ocupacion, String fotografia, String observacion) {
        this.id = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.documento = documento;
        this.correo = correo;
        this.fechaRegistro = fechaRegistro;
        this.sexo = sexo;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.contactoEmergencia = contactoEmergencia;
        this.telefonoEmergencia = telefonoEmergencia;
        this.ocupacion = ocupacion;
        this.fotografia = fotografia;
        this.observacion = observacion;
    }
    public Integer getId() {
        return id;   
    }   
    public void setId(Integer idCliente) {
        this.id = idCliente;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getDocumento() {
        return documento;
    }
    public void setDocumento(String documento) {
        this.documento = documento;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    public Short getSexo() {
        return sexo;
    }
    public void setSexo(Short sexo) {
        this.sexo = sexo;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getCiudad() {
        return ciudad;
    }   
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    public String getContactoEmergencia() {
        return contactoEmergencia;
    }
    public void setContactoEmergencia(String contactoEmergencia) {
        this.contactoEmergencia = contactoEmergencia;
    }
    public String getTelefonoEmergencia() {
        return telefonoEmergencia;
    }
    public void setTelefonoEmergencia(String telefonoEmergencia) {
        this.telefonoEmergencia = telefonoEmergencia;
    }
    public String getOcupacion() {
        return ocupacion;
    }
    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }
    public String getFotografia() {
        return fotografia;
    }
    public void setFotografia(String fotografia) {
        this.fotografia = fotografia;
    }
    public String getObservacion() {
        return observacion;
    }
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }   

}
