package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.FichaMedica;

/**
 * Acceso a las fichas médicas.
 */
public interface FichaMedicaRepository extends JpaRepository<FichaMedica, Integer> {
}
