package system_gym.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.PlanesMembresia;
import system_gym.repository.PlanesMembresiaRepository;

@Service
public class PlanesMembresiaService {

    private final PlanesMembresiaRepository planesMembresiaRepository;

    public PlanesMembresiaService(PlanesMembresiaRepository planesMembresiaRepository) {
        this.planesMembresiaRepository = planesMembresiaRepository;
    }

    @Transactional(readOnly = true)
    public List<PlanesMembresia> listar() {
        return planesMembresiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PlanesMembresia buscarPorId(Integer id) {
        return planesMembresiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado con id " + id));
    }

    @Transactional
    public PlanesMembresia guardar(PlanesMembresia plan) {
        return planesMembresiaRepository.save(plan);
    }

    @Transactional
    public void eliminar(Integer id) {
        planesMembresiaRepository.deleteById(id);
    }
}
