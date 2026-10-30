package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.Roles;

/**
 * Acceso a los roles de seguridad.
 */
public interface RolesRepository extends JpaRepository<Roles, Integer> {
}
