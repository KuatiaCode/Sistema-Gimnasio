package system_gym.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.FichaEntrenamiento;

/**
 * Acceso a las fichas de entrenamiento.
 */
public interface FichaEntrenamientoRepository extends JpaRepository<FichaEntrenamiento, Integer> {

    @Override
    @EntityGraph(attributePaths = {"cliente", "membresia", "membresia.planes", "entrenadores"})
    List<FichaEntrenamiento> findAll();

    boolean existsByMembresiaId(Integer membresiaId);
}
