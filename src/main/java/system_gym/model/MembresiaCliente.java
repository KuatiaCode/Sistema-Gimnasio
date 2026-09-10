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
@Table(name = "tb_membresias_clientes")
public class MembresiaCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_membresia")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_membresias_miembro"))
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_membresias_plan"))
    private PlanesMembresia planesMembresia;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "estado", nullable = false)
    private Short estado; // 1: Activa, 2: Vencida, 3: Congelada

    @Column(name = "precio_pagado", precision = 10, scale = 2)
    private BigDecimal precioPagado;

    @Column(name = "renovacion_automatica", nullable = false)
    private Boolean renovacionAutomatica = false;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    public MembresiaCliente() {}
    public MembresiaCliente(Integer id, Cliente cliente, PlanesMembresia planesMembresia, LocalDate fechaInicio, LocalDate fechaVencimiento, Short estado, BigDecimal precioPagado, Boolean renovacionAutomatica, String observaciones) {
        this.id = id;
        this.cliente = cliente;
        this.planesMembresia = planesMembresia;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
        this.precioPagado = precioPagado;
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
    public PlanesMembresia getPlanesMembresia() {
        return planesMembresia;
    }
    public void setPlanesMembresia(PlanesMembresia planesMembresia) {
        this.planesMembresia = planesMembresia;
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
    public BigDecimal getPrecioPagado() {
        return precioPagado;
    }
    public void setPrecioPagado(BigDecimal precioPagado) {
        this.precioPagado = precioPagado;
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
    
}
