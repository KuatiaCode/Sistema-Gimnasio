package system_gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.MembresiaCliente;

public interface MembresiaClienteRepository extends JpaRepository<MembresiaCliente, Integer> {
}
