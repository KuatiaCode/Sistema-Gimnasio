package system_gym.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.MembresiaCliente;
import system_gym.model.MembresiaEvento;
import system_gym.model.Pago;
import system_gym.model.PlanesMembresia;
import system_gym.model.TipoEventoMembresia;
import system_gym.repository.FichaEntrenamientoRepository;
import system_gym.repository.MembresiaClienteRepository;
import system_gym.repository.MembresiaEventoRepository;
import system_gym.repository.PagoRepository;

/**
 * Servicio CRUD y de renovación para las membresías contratadas por los clientes.
 */
@Service
public class MembresiaClienteService {

    private static final short ESTADO_ACTIVA = 1;
    private static final short ESTADO_VENCIDA = 2;
    private static final short ESTADO_CONGELADA = 3;
    private static final short ESTADO_INACTIVA = 4;

    private final MembresiaClienteRepository membresiaClienteRepository;
    private final MembresiaEventoRepository membresiaEventoRepository;
    private final PagoRepository pagoRepository;
    private final FichaEntrenamientoRepository fichaEntrenamientoRepository;

    public MembresiaClienteService(MembresiaClienteRepository membresiaClienteRepository,
            MembresiaEventoRepository membresiaEventoRepository,
            PagoRepository pagoRepository, FichaEntrenamientoRepository fichaEntrenamientoRepository) {
        this.membresiaClienteRepository = membresiaClienteRepository;
        this.membresiaEventoRepository = membresiaEventoRepository;
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

    @Transactional(readOnly = true)
    public List<MembresiaEvento> listarEventos(Integer membresiaId) {
        buscarPorId(membresiaId);
        return membresiaEventoRepository.findByMembresiaIdOrderByFechaEventoDesc(membresiaId);
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

        MembresiaCliente existente = membresia.getId() == null ? null : buscarPorId(membresia.getId());

        if (existente == null) {
            membresia.setPrecioAPagar(total);
        } else {
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

        if (membresia.getEstado() == null && existente != null) {
            membresia.setEstado(existente.getEstado());
        }
        if (membresia.getEstado() == null) {
            membresia.setEstado(ESTADO_ACTIVA);
        }
        if (existente != null) {
            if (Objects.equals(existente.getEstado(), ESTADO_INACTIVA)
                    && !Objects.equals(membresia.getEstado(), ESTADO_INACTIVA)) {
                throw new IllegalStateException("Una membresía inactiva no se puede reactivar desde la edición.");
            }
            if (Objects.equals(existente.getEstado(), ESTADO_CONGELADA)
                    && !Objects.equals(membresia.getEstado(), ESTADO_CONGELADA)) {
                throw new IllegalStateException("Usá la acción Descongelar para reactivar la membresía.");
            }
            membresia.setFechaCancelacion(existente.getFechaCancelacion());
            membresia.setMotivoCancelacion(existente.getMotivoCancelacion());
            membresia.setFechaCongelacionInicio(existente.getFechaCongelacionInicio());
            membresia.setFechaCongelacionFin(existente.getFechaCongelacionFin());
            membresia.setMotivoCongelacion(existente.getMotivoCongelacion());
            membresia.setDiasCongelados(existente.getDiasCongelados());
        } else if (Objects.equals(membresia.getEstado(), ESTADO_INACTIVA)) {
            throw new IllegalStateException("Usá la acción Cancelar para inactivar una membresía.");
        }
        if (membresia.getDiasCongelados() == null) {
            membresia.setDiasCongelados(0);
        }
        if (Objects.equals(membresia.getEstado(), ESTADO_ACTIVA)) {
            LocalDate hoy = LocalDate.now();
            if (membresia.getId() == null) {
                for (MembresiaCliente anterior : membresiaClienteRepository
                        .findByClienteIdAndEstadoOrderByFechaVencimientoDesc(
                                membresia.getCliente().getId(), ESTADO_ACTIVA)) {
                    if (anterior.getFechaVencimiento() != null
                            && anterior.getFechaVencimiento().isBefore(hoy)) {
                        anterior.setEstado(ESTADO_VENCIDA);
                        membresiaClienteRepository.save(anterior);
                    }
                }
            }
            boolean activaDuplicada = membresia.getId() == null
                    ? membresiaClienteRepository
                            .existsByClienteIdAndEstadoAndFechaVencimientoGreaterThanEqual(
                                    membresia.getCliente().getId(), ESTADO_ACTIVA, hoy)
                    : membresiaClienteRepository
                            .existsByClienteIdAndEstadoAndFechaVencimientoGreaterThanEqualAndIdNot(
                                    membresia.getCliente().getId(), ESTADO_ACTIVA, hoy, membresia.getId());
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

    @Transactional
    public MembresiaEvento renovar(Integer id, LocalDate nuevaFechaVencimiento, String motivo,
            String responsable) {
        MembresiaCliente membresia = buscarPorId(id);
        if (Objects.equals(membresia.getEstado(), ESTADO_INACTIVA)) {
            throw new IllegalStateException("Una membresía inactiva no se puede renovar.");
        }
        if (Objects.equals(membresia.getEstado(), ESTADO_CONGELADA)) {
            throw new IllegalStateException("Descongelá la membresía antes de renovarla.");
        }
        if (nuevaFechaVencimiento == null) {
            throw new IllegalArgumentException("La nueva fecha de vencimiento es obligatoria.");
        }
        if (nuevaFechaVencimiento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La nueva fecha de vencimiento no puede ser anterior a hoy.");
        }
        if (membresia.getFechaVencimiento() != null
                && !nuevaFechaVencimiento.isAfter(membresia.getFechaVencimiento())) {
            throw new IllegalArgumentException("La nueva fecha de vencimiento debe ser posterior a la actual.");
        }
        validarMotivo(motivo);

        LocalDate anterior = membresia.getFechaVencimiento();
        membresia.setFechaVencimiento(nuevaFechaVencimiento);
        membresia.setEstado(ESTADO_ACTIVA);
        membresia.setFechaCancelacion(null);
        membresia.setMotivoCancelacion(null);
        membresiaClienteRepository.save(membresia);

        return registrarEvento(membresia, TipoEventoMembresia.RENOVACION, motivo,
                anterior, nuevaFechaVencimiento, anterior, nuevaFechaVencimiento,
                null, membresia.getPrecioAPagar(), responsable);
    }

    @Transactional
    public MembresiaEvento cancelar(Integer id, LocalDate fechaCancelacion, String motivo,
            String responsable) {
        MembresiaCliente membresia = buscarPorId(id);
        if (Objects.equals(membresia.getEstado(), ESTADO_INACTIVA)) {
            throw new IllegalStateException("La membresía ya está inactiva.");
        }
        if (fechaCancelacion == null) {
            fechaCancelacion = LocalDate.now();
        }
        if (fechaCancelacion.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de cancelación no puede ser futura.");
        }
        if (membresia.getFechaInicio() != null
                && fechaCancelacion.isBefore(membresia.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de cancelación no puede ser anterior al inicio.");
        }
        validarMotivo(motivo);

        if (Objects.equals(membresia.getEstado(), ESTADO_CONGELADA)) {
            finalizarCongelacion(membresia, fechaCancelacion,
                    "Ajuste de vigencia por cancelación de la membresía", responsable);
        }

        LocalDate anterior = membresia.getFechaVencimiento();
        membresia.setEstado(ESTADO_INACTIVA);
        membresia.setFechaCancelacion(fechaCancelacion);
        membresia.setMotivoCancelacion(motivo.trim());
        membresiaClienteRepository.save(membresia);

        return registrarEvento(membresia, TipoEventoMembresia.CANCELACION, motivo,
                fechaCancelacion, null, anterior, anterior, null, null, responsable);
    }

    @Transactional
    public MembresiaEvento congelar(Integer id, LocalDate fechaInicio, LocalDate fechaFin,
            String motivo, String responsable) {
        MembresiaCliente membresia = buscarPorId(id);
        if (!Objects.equals(membresia.getEstado(), ESTADO_ACTIVA)) {
            throw new IllegalStateException("Solo se puede congelar una membresía activa.");
        }
        if (membresia.getFechaVencimiento() != null
                && membresia.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new IllegalStateException("La membresía está vencida.");
        }
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now();
        }
        if (fechaFin == null) {
            throw new IllegalArgumentException("La fecha de fin de la congelación es obligatoria.");
        }
        if (fechaInicio.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La congelación no puede iniciar en una fecha futura.");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior al inicio.");
        }
        if (fechaFin.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a hoy.");
        }
        validarMotivo(motivo);

        LocalDate anterior = membresia.getFechaVencimiento();
        membresia.setEstado(ESTADO_CONGELADA);
        membresia.setFechaCongelacionInicio(fechaInicio);
        membresia.setFechaCongelacionFin(fechaFin);
        membresia.setMotivoCongelacion(motivo.trim());
        membresiaClienteRepository.save(membresia);

        int dias = (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        return registrarEvento(membresia, TipoEventoMembresia.CONGELACION, motivo,
                fechaInicio, fechaFin, anterior, anterior, dias, null, responsable);
    }

    @Transactional
    public MembresiaEvento descongelar(Integer id, String responsable) {
        MembresiaCliente membresia = buscarPorId(id);
        if (!Objects.equals(membresia.getEstado(), ESTADO_CONGELADA)) {
            throw new IllegalStateException("La membresía no está congelada.");
        }
        return finalizarCongelacion(membresia, LocalDate.now(),
                "Reactivación manual de la membresía", responsable);
    }

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void actualizarEstadosMembresias() {
        LocalDate hoy = LocalDate.now();
        for (MembresiaCliente membresia : membresiaClienteRepository.findAll()) {
            if (membresia.getEstado() == null
                    || Objects.equals(membresia.getEstado(), ESTADO_INACTIVA)) {
                continue;
            }
            if (Objects.equals(membresia.getEstado(), ESTADO_CONGELADA)) {
                if (membresia.getFechaCongelacionFin() != null
                        && !membresia.getFechaCongelacionFin().isAfter(hoy)) {
                    finalizarCongelacion(membresia, hoy,
                            "Reactivación automática al finalizar la congelación", "Sistema");
                }
                continue;
            }
            short nuevoEstado = membresia.getFechaVencimiento() != null
                    && membresia.getFechaVencimiento().isBefore(hoy)
                            ? ESTADO_VENCIDA
                            : ESTADO_ACTIVA;
            if (!Objects.equals(membresia.getEstado(), nuevoEstado)) {
                membresia.setEstado(nuevoEstado);
                membresiaClienteRepository.save(membresia);
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void actualizarEstadosAlIniciar() {
        actualizarEstadosMembresias();
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
            if (Objects.equals(membresia.getEstado(), ESTADO_CONGELADA)
                    || Objects.equals(membresia.getEstado(), ESTADO_INACTIVA)) {
                continue;
            }
            LocalDate anterior = membresia.getFechaVencimiento();
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
                pagoPendiente.setFechaVencimiento(nuevaVencimiento);
                pagoPendiente.setDescuentoAplicado(0L);
                pagoRepository.save(pagoPendiente);
            }

            membresia.setFechaVencimiento(nuevaVencimiento);
            membresia.setEstado(ESTADO_ACTIVA);
            membresiaClienteRepository.save(membresia);
            registrarEvento(membresia, TipoEventoMembresia.RENOVACION,
                    "Renovación automática mensual", anterior, nuevaVencimiento,
                    anterior, nuevaVencimiento, null, membresia.getPrecioAPagar(), "Sistema");
        }
    }

    private MembresiaEvento finalizarCongelacion(MembresiaCliente membresia, LocalDate fechaEfectiva,
            String motivo, String responsable) {
        LocalDate inicio = membresia.getFechaCongelacionInicio();
        LocalDate finPlanificada = membresia.getFechaCongelacionFin();
        LocalDate fin = fechaEfectiva;
        if (finPlanificada != null && fin.isAfter(finPlanificada)) {
            fin = finPlanificada;
        }
        int dias = 0;
        if (inicio != null && fin != null && !fin.isBefore(inicio)) {
            dias = (int) ChronoUnit.DAYS.between(inicio, fin) + 1;
        }

        LocalDate anterior = membresia.getFechaVencimiento();
        LocalDate nueva = anterior == null ? null : anterior.plusDays(dias);
        membresia.setFechaVencimiento(nueva);
        membresia.setDiasCongelados((membresia.getDiasCongelados() == null
                ? 0
                : membresia.getDiasCongelados()) + dias);
        membresia.setEstado(nueva != null && nueva.isBefore(LocalDate.now())
                ? ESTADO_VENCIDA
                : ESTADO_ACTIVA);
        membresia.setFechaCongelacionInicio(null);
        membresia.setFechaCongelacionFin(null);
        membresia.setMotivoCongelacion(null);
        membresiaClienteRepository.save(membresia);

        return registrarEvento(membresia, TipoEventoMembresia.REACTIVACION, motivo,
                fechaEfectiva, null, anterior, nueva, dias, null, responsable);
    }

    private MembresiaEvento registrarEvento(MembresiaCliente membresia, TipoEventoMembresia tipo,
            String motivo, LocalDate fechaInicio, LocalDate fechaFin,
            LocalDate vencimientoAnterior, LocalDate vencimientoNueva, Integer dias,
            Long monto, String responsable) {
        MembresiaEvento evento = new MembresiaEvento();
        evento.setMembresia(membresia);
        evento.setTipo(tipo);
        evento.setFechaEvento(LocalDateTime.now());
        evento.setMotivo(motivo == null ? null : motivo.trim());
        evento.setFechaInicio(fechaInicio);
        evento.setFechaFin(fechaFin);
        evento.setFechaVencimientoAnterior(vencimientoAnterior);
        evento.setFechaVencimientoNueva(vencimientoNueva);
        evento.setDias(dias);
        evento.setMonto(monto);
        evento.setResponsable(responsable);
        return membresiaEventoRepository.save(evento);
    }

    private void validarMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio.");
        }
    }

    private LocalDate siguienteMes(LocalDate fecha, int diaAncla) {
        YearMonth mesSiguiente = YearMonth.from(fecha).plusMonths(1);
        return mesSiguiente.atDay(Math.min(diaAncla, mesSiguiente.lengthOfMonth()));
    }
}
