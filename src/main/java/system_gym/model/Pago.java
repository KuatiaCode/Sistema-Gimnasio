package system_gym.model;

import java.math.BigDecimal;
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

@Entity
@Table(name = "tb_pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_pagos_cliente"))
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_membresia", nullable = true, 
                foreignKey = @ForeignKey(name = "fk_pagos_membresia"))
    private MembresiaCliente membresia;

    @Column(name = "monto_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "metodo_pago", nullable = false)
    private Short metodoPago; // 1: Efectivo, 2: Tarjeta, 3: Transferencia

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago = LocalDateTime.now();

    @Column(name = "descuento_aplicado", precision = 10, scale = 2, nullable = false)
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;

    public Pago() {}
    public Pago(Integer id, Cliente cliente, MembresiaCliente membresia, BigDecimal montoTotal, Short metodoPago, LocalDateTime fechaPago, BigDecimal descuentoAplicado) {
        this.id = id;
        this.cliente = cliente;
        this.membresia = membresia;
        this.montoTotal = montoTotal;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.descuentoAplicado = descuentoAplicado;
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
    public BigDecimal getMontoTotal() {
        return montoTotal;
    }
    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
    public Short getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(Short metodoPago) {
        this.metodoPago = metodoPago;
    }
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }   
    public BigDecimal getDescuentoAplicado() {
        return descuentoAplicado;
    }
    public void setDescuentoAplicado(BigDecimal descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }
    
}
