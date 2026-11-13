package system_gym.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.Usuario;

/**
 * Acceso a los usuarios del sistema.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);

    boolean existsByUsuarioAndIdNot(String usuario, Integer id);

    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndIdNot(String correo, Integer id);

    boolean existsByRolesId(Integer rolesId);

    long countByRolesCodigo(String codigo);

    List<Usuario> findByRolesCodigoOrderByUsuarioAsc(String codigo);
}
