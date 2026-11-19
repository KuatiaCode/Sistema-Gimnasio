package system_gym.reporte;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * Filtros comunes enviados desde el formulario de reportes.
 */
public class ReporteFiltro {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    private Short estado;
    private Integer clienteId;
    private Short metodoPago;
    private Short prioridad;
    private Integer responsableId;
    private String texto;
    private String tipoPersonalizado;
    private List<String> columnas = new ArrayList<>();

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Short getEstado() {
        return estado;
    }

    public void setEstado(Short estado) {
        this.estado = estado;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Short getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(Short metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Short getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Short prioridad) {
        this.prioridad = prioridad;
    }

    public Integer getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Integer responsableId) {
        this.responsableId = responsableId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTipoPersonalizado() {
        return tipoPersonalizado;
    }

    public void setTipoPersonalizado(String tipoPersonalizado) {
        this.tipoPersonalizado = tipoPersonalizado;
    }

    public List<String> getColumnas() {
        return columnas;
    }

    public void setColumnas(List<String> columnas) {
        this.columnas = columnas == null ? new ArrayList<>() : columnas;
    }
}
