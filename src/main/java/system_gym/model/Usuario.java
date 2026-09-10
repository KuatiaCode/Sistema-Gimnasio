package system_gym.model;

import java.time.LocalDateTime;

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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tb_usuario", uniqueConstraints = {
    @UniqueConstraint(columnNames = "usuario"),
    @UniqueConstraint(columnNames = "correo")
})
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false, foreignKey = @ForeignKey(name = "fk_usuarios_roles"))
    private Roles roles;

    @Column(name = "usuario", length = 50, nullable = false)
    private String usuario;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "correo", length = 255, nullable = false)
    private String correo;

    @Column(name = "dos_pasos_activo", nullable = false)
    private Boolean dosPasosActivo = false;

    @Column(name = "intentos_fallidos", nullable = false)
    private Integer intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    public Usuario() {}

    public Usuario(Integer id, Roles roles, String usuario, String passwordHash, String correo, Boolean dosPasosActivo, Integer intentosFallidos, LocalDateTime bloqueadoHasta, LocalDateTime ultimoAcceso) {
        this.id = id;
        this.roles = roles;
        this.usuario = usuario;
        this.passwordHash = passwordHash;
        this.correo = correo;
        this.dosPasosActivo = dosPasosActivo;
        this.intentosFallidos = intentosFallidos;
        this.bloqueadoHasta = bloqueadoHasta;
        this.ultimoAcceso = ultimoAcceso;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;   
    }
    public Roles getRoles() {
        return roles;
    }
    public void setRoles(Roles roles) {
        this.roles = roles; 
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario; 
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;   
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public Boolean getDosPasosActivo() {
        return dosPasosActivo;
    }
    public void setDosPasosActivo(Boolean dosPasosActivo) {
        this.dosPasosActivo = dosPasosActivo;   
    }   
    public Integer getIntentosFallidos() {
        return intentosFallidos;
    }
    public void setIntentosFallidos(Integer intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }
    public LocalDateTime getBloqueadoHasta() {
        return bloqueadoHasta;
    }
    public void setBloqueadoHasta(LocalDateTime bloqueadoHasta) {
        this.bloqueadoHasta = bloqueadoHasta;
    }
    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;       
    }
    
}
