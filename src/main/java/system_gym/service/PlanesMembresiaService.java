package system_gym.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.PlanesMembresia;
import system_gym.repository.MembresiaClienteRepository;
import system_gym.repository.PlanesMembresiaRepository;

/**
 * Servicio CRUD para el catálogo de planes de membresía.
 */
@Service
public class PlanesMembresiaService {

    private final PlanesMembresiaRepository planesMembresiaRepository;
    private final MembresiaClienteRepository membresiaClienteRepository;

    public PlanesMembresiaService(PlanesMembresiaRepository planesMembresiaRepository,
            MembresiaClienteRepository membresiaClienteRepository) {
        this.planesMembresiaRepository = planesMembresiaRepository;
        this.membresiaClienteRepository = membresiaClienteRepository;
    }

    @Transactional(readOnly = true)
    public List<PlanesMembresia> listar() {
        return planesMembresiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PlanesMembresia> listarActivos() {
        return planesMembresiaRepository.findByEstado((short) 1);
    }

    @Transactional(readOnly = true)
    public PlanesMembresia buscarPorId(Integer id) {
        return planesMembresiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado con id " + id));
    }

    @Transactional
    public PlanesMembresia guardar(PlanesMembresia plan) {
        if (plan.getNombre() == null || plan.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del plan es obligatorio.");
        }
        if (plan.getDescripcion() == null || plan.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción del plan es obligatoria.");
        }
        if (plan.getPrecio() == null || plan.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio del plan no puede ser negativo.");
        }
        plan.setNombre(plan.getNombre().trim());
        plan.setDescripcion(plan.getDescripcion().trim());
        return planesMembresiaRepository.save(plan);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (membresiaClienteRepository.existsByPlanesId(id)) {
            throw new IllegalStateException("No se puede eliminar un plan asociado a una membresía.");
        }
        planesMembresiaRepository.deleteById(id);
    }
}
