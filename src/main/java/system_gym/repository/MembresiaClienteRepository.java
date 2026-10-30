package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.MembresiaCliente;

/**
 * Acceso a las membresías contratadas por los clientes.
 */
public interface MembresiaClienteRepository extends JpaRepository<MembresiaCliente, Integer> {
}
