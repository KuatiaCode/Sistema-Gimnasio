package system_gym.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.Roles;

/**
 * Acceso a los roles de seguridad.
 */
public interface RolesRepository extends JpaRepository<Roles, Integer> {

    Optional<Roles> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Integer id);
}
