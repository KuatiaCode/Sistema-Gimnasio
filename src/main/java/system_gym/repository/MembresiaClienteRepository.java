package system_gym.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import system_gym.model.MembresiaCliente;

/**
 * Acceso a las membresías contratadas por los clientes.
 */
public interface MembresiaClienteRepository extends JpaRepository<MembresiaCliente, Integer> {

    @Override
    @EntityGraph(attributePaths = {"cliente", "planes"})
    List<MembresiaCliente> findAll();

    @Override
    @EntityGraph(attributePaths = {"cliente", "planes"})
    Optional<MembresiaCliente> findById(Integer id);

    @EntityGraph(attributePaths = {"cliente", "planes"})
    List<MembresiaCliente> findByClienteIdOrderByFechaInicioDesc(Integer clienteId);

    @EntityGraph(attributePaths = {"cliente", "planes"})
    List<MembresiaCliente> findByClienteIdAndEstadoOrderByFechaVencimientoDesc(Integer clienteId, Short estado);

    @EntityGraph(attributePaths = {"cliente", "planes"})
    Optional<MembresiaCliente> findFirstByClienteIdAndEstadoOrderByFechaVencimientoDesc(
            Integer clienteId, Short estado);

    boolean existsByClienteIdAndEstadoAndFechaVencimientoGreaterThanEqual(
            Integer clienteId, Short estado, LocalDate fecha);

    boolean existsByClienteIdAndEstadoAndFechaVencimientoGreaterThanEqualAndIdNot(
            Integer clienteId, Short estado, LocalDate fecha, Integer id);

    boolean existsByPlanesId(Integer planId);

    List<MembresiaCliente> findByRenovacionAutomaticaTrueAndFechaVencimientoLessThanEqual(LocalDate fecha);
}
