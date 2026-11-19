package system_gym.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Falla;
import system_gym.model.Usuario;
import system_gym.repository.FallaRepository;

/**
 * Servicio de registro y seguimiento de fallas técnicas.
 */
@Service
public class FallaService {

    private final FallaRepository fallaRepository;
    private final UsuarioService usuarioService;

    public FallaService(FallaRepository fallaRepository, UsuarioService usuarioService) {
        this.fallaRepository = fallaRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<Falla> listar() {
        return fallaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Falla buscarPorId(Integer id) {
        return fallaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Falla no encontrada con id " + id));
    }

    @Transactional
    public Falla guardar(Falla falla, Usuario usuarioReporta, boolean esAdmin, Integer responsableId) {
        if (usuarioReporta == null) {
            throw new IllegalArgumentException("El usuario que reporta es obligatorio.");
        }

        if (falla.getId() == null) {
            falla.setUsuarioReporta(usuarioReporta);
            falla.setReportadoPor(usuarioReporta.getUsuario());
            falla.setFechaReporte(LocalDateTime.now());
            falla.setEstado((short) 1);
            falla.setResponsableAtencion(null);
            falla.setFechaCierre(null);
        } else {
            if (!esAdmin) {
                throw new IllegalStateException("Solo el administrador puede modificar una falla.");
            }
            Falla existente = buscarPorId(falla.getId());
            falla.setUsuarioReporta(existente.getUsuarioReporta());
            falla.setReportadoPor(existente.getReportadoPor());
            falla.setFechaReporte(existente.getFechaReporte());
            if (falla.getEstado() == null) {
                falla.setEstado(existente.getEstado());
            }
            falla.setResponsableAtencion(responsableId == null
                    ? null
                    : usuarioService.buscarPorId(responsableId));
            if (falla.getEstado() == 3) {
                falla.setFechaCierre(existente.getFechaCierre() != null
                        ? existente.getFechaCierre()
                        : LocalDateTime.now());
            } else {
                falla.setFechaCierre(null);
            }
        }

        falla.setAreaEquipo(limpiar(falla.getAreaEquipo()));
        falla.setDescripcion(limpiar(falla.getDescripcion()));
        falla.setObservacion(limpiar(falla.getObservacion()));
        validar(falla);
        return fallaRepository.save(falla);
    }

    private void validar(Falla falla) {
        if (textoVacio(falla.getAreaEquipo())) {
            throw new IllegalArgumentException("El área o equipo es obligatorio.");
        }
        if (falla.getAreaEquipo().length() > 100) {
            throw new IllegalArgumentException("El área o equipo no puede superar los 100 caracteres.");
        }
        if (textoVacio(falla.getDescripcion())) {
            throw new IllegalArgumentException("La descripción de la falla es obligatoria.");
        }
        if (falla.getDescripcion().length() < 10 || falla.getDescripcion().length() > 1000) {
            throw new IllegalArgumentException("La descripción debe tener entre 10 y 1000 caracteres.");
        }
        if (textoVacio(falla.getReportadoPor())) {
            throw new IllegalArgumentException("El nombre de quien reporta es obligatorio.");
        }
        if (falla.getPrioridad() == null || falla.getPrioridad() < 1 || falla.getPrioridad() > 3) {
            throw new IllegalArgumentException("La prioridad de la falla no es válida.");
        }
        if (falla.getEstado() == null || falla.getEstado() < 1 || falla.getEstado() > 3) {
            throw new IllegalArgumentException("El estado de la falla no es válido.");
        }
        if (falla.getObservacion() != null && falla.getObservacion().length() > 1000) {
            throw new IllegalArgumentException("La observación no puede superar los 1000 caracteres.");
        }
    }

    private boolean textoVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String limpiar(String texto) {
        return texto == null ? null : texto.trim();
    }
}
