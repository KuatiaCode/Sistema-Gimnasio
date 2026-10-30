package system_gym.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.Configuracion;

/**
 * Acceso a la configuración global; solo se espera una fila.
 */
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Integer> {

    Optional<Configuracion> findFirstByOrderByIdAsc();
}
