package system_gym.model;

import java.math.BigDecimal;
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
@Table(name = "tb_ficha_medica")
public class FichaMedica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_evaluaciones_miembro"))
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrenador", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_evaluaciones_entrenador"))
    private Usuario entrenador; // Apunta a tb_usuario

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha = LocalDate.now();

    @Column(name = "peso", precision = 5, scale = 2, nullable = false)
    private BigDecimal peso;

    @Column(name = "altura", precision = 5, scale = 2, nullable = false)
    private BigDecimal altura;

    @Column(name = "imc", precision = 5, scale = 2, nullable = false)
    private BigDecimal imc; 

    @Column(name = "porcentaje_grasa", precision = 5, scale = 2, nullable = false)
    private BigDecimal porcentajeGrasa;

    @Column(name = "masa_corporal", precision = 5, scale = 2, nullable = false)
    private BigDecimal masaCorporal;

    @Column(name = "presion_arterial", precision = 5, scale = 2, nullable = false)
    private BigDecimal presionArterial;

    @Column(name = "frecuencia_cardiaca", nullable = false)
    private Integer frecuenciaCardiaca;

    @Column(name = "enfermedades", columnDefinition = "TEXT", nullable = false)
    private String enfermedades;

    @Column(name = "alergias", columnDefinition = "TEXT", nullable = false)
    private String alergias;

    @Column(name = "medicamentos", columnDefinition = "TEXT", nullable = false)
    private String medicamentos;

    @Column(name = "cirugias_previas", columnDefinition = "TEXT", nullable = false)
    private String cirugiasPrevias;

    @Column(name = "lesiones", columnDefinition = "TEXT", nullable = false)
    private String lesiones;

    @Column(name = "apto_entrenar", nullable = false)
    private Boolean aptoEntrenar = true; 

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    public FichaMedica() {}
    public FichaMedica(Cliente cliente, Usuario entrenador, BigDecimal peso, BigDecimal altura, BigDecimal imc,
                     BigDecimal porcentajeGrasa, BigDecimal masaCorporal, BigDecimal presionArterial,
                     Integer frecuenciaCardiaca, String enfermedades, String alergias, String medicamentos,
                     String cirugiasPrevias, String lesiones, Boolean aptoEntrenar, String observacion) {
        this.cliente = cliente;
        this.entrenador = entrenador;
        this.peso = peso;
        this.altura = altura;
        this.imc = imc;
        this.porcentajeGrasa = porcentajeGrasa;
        this.masaCorporal = masaCorporal;
        this.presionArterial = presionArterial;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.enfermedades = enfermedades;
        this.alergias = alergias;
        this.medicamentos = medicamentos;
        this.cirugiasPrevias = cirugiasPrevias;
        this.lesiones = lesiones;
        this.aptoEntrenar = aptoEntrenar;
        this.observacion = observacion;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer idEvaluacion) {
        this.id = idEvaluacion;
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
    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public BigDecimal getPeso() {
        return peso;
    }
    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }
    public BigDecimal getAltura() {
        return altura;
    }
    public void setAltura(BigDecimal altura) {
        this.altura = altura;
    }
    public BigDecimal getImc() {
        return imc;
    }
    public void setImc(BigDecimal imc) {
        this.imc = imc;
    }
    public BigDecimal getPorcentajeGrasa() {
        return porcentajeGrasa;
    }
    public void setPorcentajeGrasa(BigDecimal porcentajeGrasa) {
        this.porcentajeGrasa = porcentajeGrasa;
    }
    public BigDecimal getMasaCorporal() {
        return masaCorporal;
    }
    public void setMasaCorporal(BigDecimal masaCorporal) {
        this.masaCorporal = masaCorporal;
    }
    public BigDecimal getPresionArterial() {
        return presionArterial;
    }
    public void setPresionArterial(BigDecimal presionArterial) {
        this.presionArterial = presionArterial;
    }
    public Integer getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }
    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }
    public String getEnfermedades() {
        return enfermedades;
    }
    public void setEnfermedades(String enfermedades) {
        this.enfermedades = enfermedades;
    }
    public String getAlergias() {
        return alergias;
    }
    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }
    public String getMedicamentos() {
        return medicamentos;
    }
    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }
    public String getCirugiasPrevias() {
        return cirugiasPrevias;
    }
    public void setCirugiasPrevias(String cirugiasPrevias) {
        this.cirugiasPrevias = cirugiasPrevias;
    }
    public String getLesiones() {
        return lesiones;
    }
    public void setLesiones(String lesiones) {
        this.lesiones = lesiones;
    }
    public Boolean getAptoEntrenar() {
        return aptoEntrenar;    
    }
    public void setAptoEntrenar(Boolean aptoEntrenar) {
        this.aptoEntrenar = aptoEntrenar;
    }
    public String getObservacion() {
        return observacion;
    }
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }   

}
