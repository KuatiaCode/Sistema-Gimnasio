package system_gym.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.MembresiaEvento;

public interface MembresiaEventoRepository extends JpaRepository<MembresiaEvento, Integer> {

    @Override
    @EntityGraph(attributePaths = {"membresia", "membresia.cliente", "membresia.planes"})
    Optional<MembresiaEvento> findById(Integer id);

    @EntityGraph(attributePaths = {"membresia", "membresia.cliente", "membresia.planes"})
    List<MembresiaEvento> findByMembresiaIdOrderByFechaEventoDesc(Integer membresiaId);
}
