package system_gym.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.PlanesMembresia;

/**
 * Acceso al catálogo de planes de membresía.
 */
public interface PlanesMembresiaRepository extends JpaRepository<PlanesMembresia, Integer> {

    List<PlanesMembresia> findByEstado(Short estado);
}
