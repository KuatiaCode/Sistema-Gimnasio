package system_gym.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.FichaMedica;

/**
 * Acceso a las fichas médicas.
 */
public interface FichaMedicaRepository extends JpaRepository<FichaMedica, Integer> {

    Optional<FichaMedica> findFirstByClienteIdOrderByFechaDescIdDesc(Integer clienteId);
}
