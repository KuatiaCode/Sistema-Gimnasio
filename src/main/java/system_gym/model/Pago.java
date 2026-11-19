package system_gym.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

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

/**
 * Pago realizado o pendiente de un cliente.
 * Siempre queda asociado a una membresía del mismo cliente.
 */
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
    @JoinColumn(name = "id_membresia", nullable = false,
                foreignKey = @ForeignKey(name = "fk_pagos_membresia"))
    private MembresiaCliente membresia;

    @Column(name = "estado_pago", nullable = false)
    private Short estadoPago = 2; // 1: Pendiente, 2: Pagado, 3: Anulado

    @Column(name = "monto_total", nullable = false)
    private Long montoTotal;

    @Column(name = "metodo_pago")
    private Short metodoPago; // 1: Efectivo, 2: Tarjeta, 3: Transferencia

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "descuento_aplicado", nullable = false)
    private Long descuentoAplicado = 0L;

    public Pago() {}

    public Pago(Integer id, Cliente cliente, MembresiaCliente membresia, Short estadoPago,
            Long montoTotal, Short metodoPago, LocalDateTime fechaPago, LocalDate fechaVencimiento,
            Long descuentoAplicado) {
        this.id = id;
        this.cliente = cliente;
        this.membresia = membresia;
        this.estadoPago = estadoPago;
        this.montoTotal = montoTotal;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.fechaVencimiento = fechaVencimiento;
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

    public Short getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(Short estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Long getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Long montoTotal) {
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

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Long getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public void setDescuentoAplicado(Long descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }
}
