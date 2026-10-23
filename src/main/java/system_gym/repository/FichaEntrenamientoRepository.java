package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.FichaEntrenamiento;

public interface FichaEntrenamientoRepository extends JpaRepository<FichaEntrenamiento, Integer> {
}
