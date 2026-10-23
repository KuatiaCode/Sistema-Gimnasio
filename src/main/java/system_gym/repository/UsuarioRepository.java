package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
