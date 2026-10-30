package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.Cliente;

/**
 * Acceso a los clientes del gimnasio.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
