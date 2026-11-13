package system_gym.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.MembresiaCliente;
import system_gym.model.Pago;
import system_gym.model.PlanesMembresia;
import system_gym.repository.FichaEntrenamientoRepository;
import system_gym.repository.MembresiaClienteRepository;
import system_gym.repository.PagoRepository;

/**
 * Servicio CRUD y de renovación para las membresías contratadas por los clientes.
 */
@Service
public class MembresiaClienteService {

    private final MembresiaClienteRepository membresiaClienteRepository;
    private final PagoRepository pagoRepository;
    private final FichaEntrenamientoRepository fichaEntrenamientoRepository;

    public MembresiaClienteService(MembresiaClienteRepository membresiaClienteRepository,
            PagoRepository pagoRepository, FichaEntrenamientoRepository fichaEntrenamientoRepository) {
        this.membresiaClienteRepository = membresiaClienteRepository;
        this.pagoRepository = pagoRepository;
        this.fichaEntrenamientoRepository = fichaEntrenamientoRepository;
    }

    @Transactional(readOnly = true)
    public List<MembresiaCliente> listar() {
        return membresiaClienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<MembresiaCliente> listarPorCliente(Integer clienteId) {
        return membresiaClienteRepository.findByClienteIdOrderByFechaInicioDesc(clienteId);
    }

    @Transactional(readOnly = true)
    public Optional<MembresiaCliente> buscarActivaPorCliente(Integer clienteId) {
        return membresiaClienteRepository.findFirstByClienteIdAndEstadoOrderByFechaVencimientoDesc(
                clienteId, (short) 1);
    }

    @Transactional(readOnly = true)
    public MembresiaCliente buscarPorId(Integer id) {
        return membresiaClienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada con id " + id));
    }

    @Transactional
    public MembresiaCliente guardar(MembresiaCliente membresia) {
        if (membresia.getCliente() == null) {
            throw new IllegalArgumentException("El cliente de la membresía es obligatorio.");
        }
        if (membresia.getPlanes() == null || membresia.getPlanes().isEmpty()) {
            throw new IllegalArgumentException("Seleccioná al menos un plan para la membresía.");
        }
        if (membresia.getFechaInicio() == null || membresia.getFechaVencimiento() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y vencimiento son obligatorias.");
        }
        if (membresia.getFechaVencimiento().isBefore(membresia.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior al inicio.");
        }

        long total = membresia.getPlanes().stream()
                .map(PlanesMembresia::getPrecio)
                .filter(precio -> precio != null)
                .mapToLong(Long::longValue)
                .sum();

        if (membresia.getId() == null) {
            membresia.setPrecioAPagar(total);
        } else {
            MembresiaCliente existente = buscarPorId(membresia.getId());
            Set<Integer> planesAnteriores = existente.getPlanes().stream()
                    .map(PlanesMembresia::getId)
                    .collect(Collectors.toSet());
            Set<Integer> planesNuevos = membresia.getPlanes().stream()
                    .map(PlanesMembresia::getId)
                    .collect(Collectors.toSet());
            // Si los planes no cambiaron, se conserva el total histórico.
            membresia.setPrecioAPagar(planesAnteriores.equals(planesNuevos)
                    ? existente.getPrecioAPagar()
                    : total);
        }

        if (membresia.getEstado() == null) {
            membresia.setEstado((short) 1);
        }
        if (membresia.getEstado() == 1) {
            boolean activaDuplicada = membresia.getId() == null
                    ? membresiaClienteRepository.existsByClienteIdAndEstado(
                            membresia.getCliente().getId(), (short) 1)
                    : membresiaClienteRepository.existsByClienteIdAndEstadoAndIdNot(
                            membresia.getCliente().getId(), (short) 1, membresia.getId());
            if (activaDuplicada) {
                throw new IllegalStateException("El cliente ya tiene una membresía activa.");
            }
        }
        membresia.setRenovacionAutomatica(Boolean.TRUE.equals(membresia.getRenovacionAutomatica()));
        return membresiaClienteRepository.save(membresia);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (pagoRepository.existsByMembresiaId(id)) {
            throw new IllegalStateException("No se puede eliminar una membresía con pagos asociados.");
        }
        if (fichaEntrenamientoRepository.existsByMembresiaId(id)) {
            throw new IllegalStateException("No se puede eliminar una membresía con fichas asociadas.");
        }
        membresiaClienteRepository.deleteById(id);
    }

    /**
     * Renueva mensualmente las membresías automáticas vencidas y genera el pago pendiente.
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void renovarMembresiasAutomaticas() {
        LocalDate hoy = LocalDate.now();
        List<MembresiaCliente> vencidas = membresiaClienteRepository
                .findByRenovacionAutomaticaTrueAndFechaVencimientoLessThanEqual(hoy);

        for (MembresiaCliente membresia : vencidas) {
            if (membresia.getEstado() != null && membresia.getEstado() == 3) {
                continue;
            }
            LocalDate diaAncla = membresia.getFechaInicio() != null
                    ? membresia.getFechaInicio()
                    : membresia.getFechaVencimiento();
            int dia = diaAncla.getDayOfMonth();
            LocalDate nuevaVencimiento = membresia.getFechaVencimiento();

            while (!nuevaVencimiento.isAfter(hoy)) {
                nuevaVencimiento = siguienteMes(nuevaVencimiento, dia);
                Pago pagoPendiente = new Pago();
                pagoPendiente.setCliente(membresia.getCliente());
                pagoPendiente.setMembresia(membresia);
                pagoPendiente.setEstadoPago((short) 1);
                pagoPendiente.setMontoTotal(membresia.getPrecioAPagar());
                pagoPendiente.setDescuentoAplicado(0L);
                pagoRepository.save(pagoPendiente);
            }

            membresia.setFechaVencimiento(nuevaVencimiento);
            membresia.setEstado((short) 1);
            membresiaClienteRepository.save(membresia);
        }
    }

    private LocalDate siguienteMes(LocalDate fecha, int diaAncla) {
        YearMonth mesSiguiente = YearMonth.from(fecha).plusMonths(1);
        return mesSiguiente.atDay(Math.min(diaAncla, mesSiguiente.lengthOfMonth()));
    }
}
