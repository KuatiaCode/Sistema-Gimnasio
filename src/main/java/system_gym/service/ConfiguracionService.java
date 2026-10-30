package system_gym.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Configuracion;
import system_gym.repository.ConfiguracionRepository;

/**
 * Servicio de la configuración global del gimnasio.
 * Garantiza que solo exista y se reutilice un registro de configuración.
 */
@Service
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;

    public ConfiguracionService(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    @Transactional(readOnly = true)
    public Configuracion obtenerConfiguracion() {
        // Si aún no hay fila, se devuelve un objeto con valores mínimos para el formulario.
        return configuracionRepository.findFirstByOrderByIdAsc().orElseGet(() -> {
            Configuracion configuracion = new Configuracion();
            configuracion.setTiempoSesion((short) 30);
            return configuracion;
        });
    }

    @Transactional
    public Configuracion guardar(Configuracion configuracion) {
        // Se reutiliza el id existente para actualizar en lugar de crear otra fila.
        configuracionRepository.findFirstByOrderByIdAsc().ifPresentOrElse(
                existente -> configuracion.setId(existente.getId()),
                () -> configuracion.setId(null));
        return configuracionRepository.save(configuracion);
    }
}
