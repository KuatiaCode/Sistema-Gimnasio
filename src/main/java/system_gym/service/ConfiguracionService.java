package system_gym.service;

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
    public Configuracion obtenerConfiguracion() {
        return configuracionRepository.findFirstByOrderByIdAsc().orElseGet(() -> {
            Configuracion configuracion = new Configuracion();
            configuracion.setTiempoSesion((short) 30);
            return configuracion;
        });
    }

    @Transactional
    public Configuracion guardar(Configuracion configuracion) {
        configuracionRepository.findFirstByOrderByIdAsc().ifPresentOrElse(
                existente -> configuracion.setId(existente.getId()),
                () -> configuracion.setId(null));
        return configuracionRepository.save(configuracion);
    }
}
