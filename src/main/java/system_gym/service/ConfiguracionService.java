package system_gym.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Configuracion;
import system_gym.repository.ConfiguracionRepository;

@Service
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;

    public ConfiguracionService(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    @Transactional(readOnly = true)
    public List<Configuracion> listar() {
        return configuracionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Configuracion buscarPorId(Integer id) {
        return configuracionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Configuración no encontrada con id " + id));
    }

    @Transactional
    public Configuracion guardar(Configuracion configuracion) {
        return configuracionRepository.save(configuracion);
    }

    @Transactional
    public void eliminar(Integer id) {
        configuracionRepository.deleteById(id);
    }
}
