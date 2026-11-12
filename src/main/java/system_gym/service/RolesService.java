package system_gym.service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Roles;
import system_gym.repository.RolesRepository;
import system_gym.repository.UsuarioRepository;

/**
 * Servicio CRUD para los roles de seguridad.
 */
@Service
public class RolesService {

    private static final Set<String> CODIGOS_SISTEMA = Set.of("ADMIN", "RECEPCIONISTA", "ENTRENADOR");

    private final RolesRepository rolesRepository;
    private final UsuarioRepository usuarioRepository;

    public RolesService(RolesRepository rolesRepository, UsuarioRepository usuarioRepository) {
        this.rolesRepository = rolesRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Roles> listar() {
        return rolesRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Roles buscarPorId(Integer id) {
        return rolesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public boolean existeCodigo(String codigo, Integer id) {
        if (id == null) {
            return rolesRepository.existsByCodigo(codigo);
        }
        return rolesRepository.existsByCodigoAndIdNot(codigo, id);
    }

    @Transactional
    public Roles guardar(Roles roles) {
        String codigo = roles.getCodigo().trim().toUpperCase(Locale.ROOT);
        if (roles.getId() != null) {
            Roles existente = buscarPorId(roles.getId());
            if (CODIGOS_SISTEMA.contains(existente.getCodigo()) && !existente.getCodigo().equals(codigo)) {
                throw new IllegalStateException("El código de un rol base del sistema no se puede cambiar.");
            }
        }
        roles.setCodigo(codigo);
        roles.setNombre(roles.getNombre().trim());
        roles.setDescripcion(roles.getDescripcion().trim());
        return rolesRepository.save(roles);
    }

    @Transactional
    public void eliminar(Integer id) {
        Roles rol = buscarPorId(id);
        if (CODIGOS_SISTEMA.contains(rol.getCodigo())) {
            throw new IllegalStateException("Los roles base del sistema no se pueden eliminar.");
        }
        if (usuarioRepository.existsByRolesId(id)) {
            throw new IllegalStateException("No se puede eliminar un rol asignado a usuarios.");
        }
        rolesRepository.deleteById(id);
    }
}
