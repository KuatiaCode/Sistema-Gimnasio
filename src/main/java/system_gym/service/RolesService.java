package system_gym.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Roles;
import system_gym.repository.RolesRepository;

/**
 * Servicio CRUD para los roles de seguridad.
 */
@Service
public class RolesService {

    private final RolesRepository rolesRepository;

    public RolesService(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
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

    @Transactional
    public Roles guardar(Roles roles) {
        return rolesRepository.save(roles);
    }

    @Transactional
    public void eliminar(Integer id) {
        rolesRepository.deleteById(id);
    }
}
