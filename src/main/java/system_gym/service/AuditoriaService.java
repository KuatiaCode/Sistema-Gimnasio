package system_gym.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.RegistroAuditoria;
import system_gym.repository.RegistroAuditoriaRepository;

/**
 * Servicio de consulta y registro de la bitácora de peticiones.
 */
@Service
public class AuditoriaService {

    private final RegistroAuditoriaRepository registroAuditoriaRepository;

    public AuditoriaService(RegistroAuditoriaRepository registroAuditoriaRepository) {
        this.registroAuditoriaRepository = registroAuditoriaRepository;
    }

    @Transactional
    public void registrar(RegistroAuditoria registro) {
        registroAuditoriaRepository.save(registro);
    }

    @Transactional(readOnly = true)
    public Page<RegistroAuditoria> listar(Pageable pageable) {
        return registroAuditoriaRepository.findAll(pageable);
    }
}
