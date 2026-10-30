package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.FichaEntrenamiento;

/**
 * Acceso a las fichas de entrenamiento.
 */
public interface FichaEntrenamientoRepository extends JpaRepository<FichaEntrenamiento, Integer> {
}
