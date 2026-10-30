package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.Usuario;

/**
 * Acceso a los usuarios del sistema.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
