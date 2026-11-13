package system_gym.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.FichaEntrenamiento;
import system_gym.repository.FichaEntrenamientoRepository;

/**
 * Servicio CRUD para las fichas de entrenamiento.
 */
@Service
public class FichaEntrenamientoService {

    private final FichaEntrenamientoRepository fichaEntrenamientoRepository;
    private final FichaMedicaService fichaMedicaService;

    public FichaEntrenamientoService(FichaEntrenamientoRepository fichaEntrenamientoRepository,
            FichaMedicaService fichaMedicaService) {
        this.fichaEntrenamientoRepository = fichaEntrenamientoRepository;
        this.fichaMedicaService = fichaMedicaService;
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
        if (ficha.getCliente() == null) {
            throw new IllegalArgumentException("El cliente de la ficha es obligatorio.");
        }
        if (ficha.getMembresia() == null) {
            throw new IllegalArgumentException("La membresía de la ficha es obligatoria.");
        }
        if (ficha.getMembresia().getCliente() == null
                || !ficha.getMembresia().getCliente().getId().equals(ficha.getCliente().getId())) {
            throw new IllegalArgumentException("La membresía seleccionada no pertenece al cliente.");
        }
        FichaEntrenamiento existente = ficha.getId() == null ? null : buscarPorId(ficha.getId());
        boolean cambiaCliente = existente == null
                || !existente.getCliente().getId().equals(ficha.getCliente().getId());
        if (cambiaCliente && !fichaMedicaService.tieneAptitudVigente(ficha.getCliente().getId())) {
            throw new IllegalStateException(
                    "El cliente no tiene una ficha médica vigente que confirme aptitud para entrenar.");
        }

        if (ficha.getEntrenadores() == null || ficha.getEntrenadores().isEmpty()) {
            throw new IllegalArgumentException("Seleccioná al menos un entrenador.");
        }
        if (ficha.getDiasEntrenamiento() == null || ficha.getDiasEntrenamiento().isEmpty()) {
            throw new IllegalArgumentException("Seleccioná al menos un día de entrenamiento.");
        }
        if (ficha.getDiasEntrenamiento().stream().anyMatch(dia -> dia == null || dia < 1 || dia > 7)) {
            throw new IllegalArgumentException("Los días de entrenamiento no son válidos.");
        }

        if (existente == null) {
            if (ficha.getMembresia().getEstado() == null || ficha.getMembresia().getEstado() != 1) {
                throw new IllegalStateException("El cliente no tiene una membresía activa.");
            }
            ficha.setFechaCreacion(LocalDateTime.now());
        } else {
            boolean cambiaMembresia = !existente.getMembresia().getId().equals(ficha.getMembresia().getId());
            if (cambiaMembresia && (ficha.getMembresia().getEstado() == null
                    || ficha.getMembresia().getEstado() != 1)) {
                throw new IllegalStateException("La nueva membresía debe estar activa.");
            }
            // La fecha de creación es histórica y no se modifica al editar.
            ficha.setFechaCreacion(existente.getFechaCreacion());
        }
        return fichaEntrenamientoRepository.save(ficha);
    }

    @Transactional
    public void eliminar(Integer id) {
        fichaEntrenamientoRepository.deleteById(id);
    }
}
