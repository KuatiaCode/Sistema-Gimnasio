package system_gym.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.FichaMedica;
import system_gym.repository.FichaMedicaRepository;

/**
 * Servicio CRUD y de validación para las fichas médicas.
 */
@Service
public class FichaMedicaService {

    private final FichaMedicaRepository fichaMedicaRepository;

    public FichaMedicaService(FichaMedicaRepository fichaMedicaRepository) {
        this.fichaMedicaRepository = fichaMedicaRepository;
    }

    @Transactional(readOnly = true)
    public List<FichaMedica> listar() {
        return fichaMedicaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public FichaMedica buscarPorId(Integer id) {
        return fichaMedicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ficha médica no encontrada con id " + id));
    }

    /**
     * Indica si la ficha médica más reciente del cliente confirma su aptitud física.
     */
    @Transactional(readOnly = true)
    public boolean tieneAptitudVigente(Integer clienteId) {
        if (clienteId == null) {
            return false;
        }
        return fichaMedicaRepository.findFirstByClienteIdOrderByFechaDescIdDesc(clienteId)
                .map(ficha -> Boolean.TRUE.equals(ficha.getAptoEntrenar()))
                .orElse(false);
    }

    @Transactional
    public FichaMedica guardar(FichaMedica ficha) {
        if (ficha.getId() == null) {
            ficha.setFecha(LocalDateTime.now());
        } else {
            FichaMedica existente = buscarPorId(ficha.getId());
            // La fecha y hora de registro es histórica y no se modifica al editar.
            ficha.setFecha(existente.getFecha() != null ? existente.getFecha() : LocalDateTime.now());
        }
        validarCamposObligatorios(ficha);
        ficha.setAptoEntrenar(Boolean.TRUE.equals(ficha.getAptoEntrenar()));
        return fichaMedicaRepository.save(ficha);
    }

    @Transactional
    public void eliminar(Integer id) {
        fichaMedicaRepository.deleteById(id);
    }

    private void validarCamposObligatorios(FichaMedica ficha) {
        if (ficha.getCliente() == null) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }
        if (ficha.getEntrenador() == null) {
            throw new IllegalArgumentException("El entrenador es obligatorio.");
        }
        if (ficha.getPeso() == null) {
            throw new IllegalArgumentException("El peso es obligatorio.");
        }
        if (ficha.getAltura() == null) {
            throw new IllegalArgumentException("La altura es obligatoria.");
        }
        if (ficha.getImc() == null) {
            throw new IllegalArgumentException("El IMC es obligatorio.");
        }
        if (ficha.getPorcentajeGrasa() == null) {
            throw new IllegalArgumentException("El porcentaje de grasa es obligatorio.");
        }
        if (ficha.getMasaCorporal() == null) {
            throw new IllegalArgumentException("La masa corporal es obligatoria.");
        }
        if (ficha.getPresionArterial() == null) {
            throw new IllegalArgumentException("La presión arterial es obligatoria.");
        }
        if (ficha.getFrecuenciaCardiaca() == null) {
            throw new IllegalArgumentException("La frecuencia cardíaca es obligatoria.");
        }
        if (textoVacio(ficha.getEnfermedades())) {
            throw new IllegalArgumentException("El campo Enfermedades es obligatorio.");
        }
        if (textoVacio(ficha.getAlergias())) {
            throw new IllegalArgumentException("El campo Alergias es obligatorio.");
        }
        if (textoVacio(ficha.getMedicamentos())) {
            throw new IllegalArgumentException("El campo Medicamentos es obligatorio.");
        }
        if (textoVacio(ficha.getCirugiasPrevias())) {
            throw new IllegalArgumentException("El campo Cirugías previas es obligatorio.");
        }
        if (textoVacio(ficha.getLesiones())) {
            throw new IllegalArgumentException("El campo Lesiones es obligatorio.");
        }
        if (esNegativo(ficha.getPeso()) || esNegativo(ficha.getAltura()) || esNegativo(ficha.getImc())
                || esNegativo(ficha.getPorcentajeGrasa()) || esNegativo(ficha.getMasaCorporal())
                || esNegativo(ficha.getPresionArterial())) {
            throw new IllegalArgumentException("Los valores numéricos no pueden ser negativos.");
        }
        if (ficha.getFrecuenciaCardiaca() < 0) {
            throw new IllegalArgumentException("La frecuencia cardíaca no puede ser negativa.");
        }
    }

    private boolean textoVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private boolean esNegativo(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) < 0;
    }
}
