package system_gym.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.FichaEntrenamiento;
import system_gym.repository.FichaEntrenamientoRepository;

@Service
public class FichaEntrenamientoService {

    private final FichaEntrenamientoRepository fichaEntrenamientoRepository;

    public FichaEntrenamientoService(FichaEntrenamientoRepository fichaEntrenamientoRepository) {
        this.fichaEntrenamientoRepository = fichaEntrenamientoRepository;
    }

    @Transactional(readOnly = true)
    public List<FichaEntrenamiento> listar() {
        return fichaEntrenamientoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public FichaEntrenamiento buscarPorId(Integer id) {
        return fichaEntrenamientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ficha de entrenamiento no encontrada con id " + id));
    }

    @Transactional
    public FichaEntrenamiento guardar(FichaEntrenamiento ficha) {
        return fichaEntrenamientoRepository.save(ficha);
    }

    @Transactional
    public void eliminar(Integer id) {
        fichaEntrenamientoRepository.deleteById(id);
    }
}
