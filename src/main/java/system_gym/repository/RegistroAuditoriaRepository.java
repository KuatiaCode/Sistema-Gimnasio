package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.RegistroAuditoria;

/**
 * Acceso a los registros de auditoría.
 */
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Integer> {
}
