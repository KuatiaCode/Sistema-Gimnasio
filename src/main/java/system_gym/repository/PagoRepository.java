package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.Pago;

/**
 * Acceso a los pagos registrados.
 */
public interface PagoRepository extends JpaRepository<Pago, Integer> {
}
