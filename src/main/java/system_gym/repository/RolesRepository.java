package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.Roles;

public interface RolesRepository extends JpaRepository<Roles, Integer> {
}
