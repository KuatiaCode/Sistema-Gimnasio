package system_gym.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Pago;
import system_gym.repository.PagoRepository;

/**
 * Servicio CRUD para los pagos registrados.
 */
@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Transactional(readOnly = true)
    public List<Pago> listar() {
        return pagoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pago buscarPorId(Integer id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado con id " + id));
    }

    @Transactional
    public Pago guardar(Pago pago) {
        if (pago.getCliente() == null) {
            throw new IllegalArgumentException("El cliente del pago es obligatorio.");
        }
        if (pago.getMembresia() == null) {
            throw new IllegalArgumentException("La membresía del pago es obligatoria.");
        }
        if (pago.getMembresia().getCliente() == null
                || !pago.getMembresia().getCliente().getId().equals(pago.getCliente().getId())) {
            throw new IllegalArgumentException("La membresía seleccionada no pertenece al cliente.");
        }
        if (pago.getFechaVencimiento() == null) {
            pago.setFechaVencimiento(pago.getMembresia().getFechaVencimiento());
        }
        if (pago.getFechaVencimiento() == null) {
            throw new IllegalArgumentException("La fecha de vencimiento del pago es obligatoria.");
        }
        if (pago.getMontoTotal() == null || pago.getMontoTotal() < 0) {
            throw new IllegalArgumentException("El monto total no puede ser negativo.");
        }
        if (pago.getDescuentoAplicado() == null) {
            pago.setDescuentoAplicado(0L);
        } else if (pago.getDescuentoAplicado() < 0) {
            throw new IllegalArgumentException("El descuento aplicado no puede ser negativo.");
        }
        if (pago.getEstadoPago() == null) {
            pago.setEstadoPago((short) 2);
        }
        if (pago.getEstadoPago() == 2) {
            if (pago.getMetodoPago() == null) {
                throw new IllegalArgumentException("El método de pago es obligatorio para un pago realizado.");
            }
            if (pago.getFechaPago() == null) {
                pago.setFechaPago(LocalDateTime.now());
            }
        }
        return pagoRepository.save(pago);
    }

    @Transactional
    public void eliminar(Integer id) {
        pagoRepository.deleteById(id);
    }
}
