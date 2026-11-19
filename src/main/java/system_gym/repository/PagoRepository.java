package system_gym.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.Pago;

/**
 * Acceso a los pagos registrados.
 */
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    @Override
    @EntityGraph(attributePaths = {"cliente", "membresia", "membresia.planes"})
    List<Pago> findAll();

    boolean existsByMembresiaId(Integer membresiaId);

    boolean existsByMembresiaIdAndEstadoPago(Integer membresiaId, Short estadoPago);
}
