package system_gym.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.FichaMedica;

/**
 * Acceso a las fichas médicas.
 */
public interface FichaMedicaRepository extends JpaRepository<FichaMedica, Integer> {

    @Override
    @EntityGraph(attributePaths = {"cliente", "entrenador"})
    List<FichaMedica> findAll();

    Optional<FichaMedica> findFirstByClienteIdOrderByFechaDescIdDesc(Integer clienteId);
}
