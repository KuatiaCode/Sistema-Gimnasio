package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.RegistroAuditoria;

public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Integer> {
}
