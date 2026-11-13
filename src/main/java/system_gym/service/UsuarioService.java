package system_gym.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Usuario;
import system_gym.repository.UsuarioRepository;

/**
 * Servicio CRUD para los usuarios del sistema.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarEntrenadores() {
        return usuarioRepository.findByRolesCodigoOrderByUsuarioAsc("ENTRENADOR");
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + id));
    }

    @Transactional
    public Usuario guardar(Usuario usuario, String passwordPlano) {
        usuario.setUsuario(usuario.getUsuario().trim());
        usuario.setCorreo(usuario.getCorreo().trim());

        boolean usuarioDuplicado = usuario.getId() == null
                ? usuarioRepository.existsByUsuario(usuario.getUsuario())
                : usuarioRepository.existsByUsuarioAndIdNot(usuario.getUsuario(), usuario.getId());
        if (usuarioDuplicado) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre.");
        }

        boolean correoDuplicado = usuario.getId() == null
                ? usuarioRepository.existsByCorreo(usuario.getCorreo())
                : usuarioRepository.existsByCorreoAndIdNot(usuario.getCorreo(), usuario.getId());
        if (correoDuplicado) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        }

        if (usuario.getId() == null) {
            if (passwordPlano == null || passwordPlano.isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria para un usuario nuevo.");
            }
            usuario.setPasswordHash(passwordEncoder.encode(passwordPlano));
            usuario.setDosPasosActivo(Boolean.TRUE.equals(usuario.getDosPasosActivo()));
            usuario.setIntentosFallidos(0);
        } else {
            Usuario existente = buscarPorId(usuario.getId());
            if (passwordPlano != null && !passwordPlano.isBlank()) {
                usuario.setPasswordHash(passwordEncoder.encode(passwordPlano));
            } else {
                usuario.setPasswordHash(existente.getPasswordHash());
            }
            boolean dejaDeSerAdmin = "ADMIN".equals(existente.getRoles().getCodigo())
                    && !"ADMIN".equals(usuario.getRoles().getCodigo());
            if (dejaDeSerAdmin && usuarioRepository.countByRolesCodigo("ADMIN") <= 1) {
                throw new IllegalStateException("No se puede quitar el rol al último administrador.");
            }
            // El formulario no edita los datos de bloqueo; se conservan los existentes.
            usuario.setIntentosFallidos(existente.getIntentosFallidos());
            usuario.setBloqueadoHasta(existente.getBloqueadoHasta());
            usuario.setUltimoAcceso(existente.getUltimoAcceso());
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Integer id) {
        Usuario usuario = buscarPorId(id);
        if ("ADMIN".equals(usuario.getRoles().getCodigo())
                && usuarioRepository.countByRolesCodigo("ADMIN") <= 1) {
            throw new IllegalStateException("No se puede eliminar el último administrador.");
        }
        usuarioRepository.deleteById(id);
    }
}
