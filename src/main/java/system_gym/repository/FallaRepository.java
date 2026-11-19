package system_gym.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.Falla;

/**
 * Acceso a las fallas técnicas reportadas.
 */
public interface FallaRepository extends JpaRepository<Falla, Integer> {

    @Override
    @EntityGraph(attributePaths = {"usuarioReporta", "responsableAtencion"})
    List<Falla> findAll();

    @EntityGraph(attributePaths = {"usuarioReporta", "responsableAtencion"})
    List<Falla> findByEstadoOrderByFechaReporteDesc(Short estado);
}
