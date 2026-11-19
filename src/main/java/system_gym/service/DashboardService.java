package system_gym.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Falla;
import system_gym.model.FichaEntrenamiento;
import system_gym.model.FichaMedica;
import system_gym.model.MembresiaCliente;
import system_gym.model.Pago;
import system_gym.reporte.EstadoMembresia;
import system_gym.reporte.EstadoMembresiaUtil;
import system_gym.repository.ClienteRepository;
import system_gym.repository.FallaRepository;
import system_gym.repository.FichaEntrenamientoRepository;
import system_gym.repository.FichaMedicaRepository;
import system_gym.repository.MembresiaClienteRepository;
import system_gym.repository.PagoRepository;

/**
 * Calcula las métricas operativas y de entrenamiento del dashboard.
 */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final MembresiaClienteRepository membresiaRepository;
    private final PagoRepository pagoRepository;
    private final FallaRepository fallaRepository;
    private final FichaMedicaRepository fichaMedicaRepository;
    private final FichaEntrenamientoRepository fichaEntrenamientoRepository;

    public DashboardService(ClienteRepository clienteRepository,
            MembresiaClienteRepository membresiaRepository,
            PagoRepository pagoRepository,
            FallaRepository fallaRepository,
            FichaMedicaRepository fichaMedicaRepository,
            FichaEntrenamientoRepository fichaEntrenamientoRepository) {
        this.clienteRepository = clienteRepository;
        this.membresiaRepository = membresiaRepository;
        this.pagoRepository = pagoRepository;
        this.fallaRepository = fallaRepository;
        this.fichaMedicaRepository = fichaMedicaRepository;
        this.fichaEntrenamientoRepository = fichaEntrenamientoRepository;
    }

    public DashboardResumen resumen() {
        LocalDate hoy = LocalDate.now();
        YearMonth mesActual = YearMonth.from(hoy);

        long activas = 0;
        long vencidas = 0;
        long congeladas = 0;
        for (MembresiaCliente membresia : ultimaMembresiaPorCliente(membresiaRepository.findAll()).values()) {
            EstadoMembresia estado = EstadoMembresiaUtil.calcular(membresia, hoy);
            if (estado == EstadoMembresia.ACTIVA) {
                activas++;
            } else if (estado == EstadoMembresia.VENCIDA) {
                vencidas++;
            } else if (estado == EstadoMembresia.CONGELADA) {
                congeladas++;
            }
        }

        List<Pago> pagos = pagoRepository.findAll();
        long pendientesVencidos = 0;
        long montoAdeudado = 0;
        long ingresosMes = 0;
        for (Pago pago : pagos) {
            if (pago.getEstadoPago() != null && pago.getEstadoPago() == 1
                    && pago.getFechaVencimiento() != null
                    && pago.getFechaVencimiento().isBefore(hoy)) {
                pendientesVencidos++;
                montoAdeudado += pago.getMontoTotal() == null ? 0 : pago.getMontoTotal();
            }
            if (pago.getEstadoPago() != null && pago.getEstadoPago() == 2
                    && pago.getFechaPago() != null
                    && YearMonth.from(pago.getFechaPago()).equals(mesActual)) {
                ingresosMes += pago.getMontoTotal() == null ? 0 : pago.getMontoTotal();
            }
        }

        long fallasAbiertas = fallaRepository.findAll().stream()
                .filter(falla -> falla.getEstado() != null && falla.getEstado() != 3)
                .count();

        List<FichaMedica> evaluaciones = fichaMedicaRepository.findAll().stream()
                .filter(ficha -> enMes(ficha.getFecha(), mesActual))
                .toList();
        List<FichaEntrenamiento> entrenamientos = fichaEntrenamientoRepository.findAll().stream()
                .filter(ficha -> enMes(ficha.getFechaCreacion(), mesActual))
                .toList();
        Set<Integer> clientesAtendidos = new HashSet<>();
        evaluaciones.stream()
                .filter(ficha -> ficha.getCliente() != null)
                .forEach(ficha -> clientesAtendidos.add(ficha.getCliente().getId()));
        entrenamientos.stream()
                .filter(ficha -> ficha.getCliente() != null)
                .forEach(ficha -> clientesAtendidos.add(ficha.getCliente().getId()));

        return new DashboardResumen(
                clienteRepository.count(),
                activas,
                vencidas,
                congeladas,
                pendientesVencidos,
                montoAdeudado,
                ingresosMes,
                fallasAbiertas,
                evaluaciones.size(),
                entrenamientos.size(),
                clientesAtendidos.size());
    }

    private Map<Integer, MembresiaCliente> ultimaMembresiaPorCliente(List<MembresiaCliente> membresias) {
        Map<Integer, MembresiaCliente> ultimas = new HashMap<>();
        for (MembresiaCliente membresia : membresias) {
            if (membresia.getCliente() == null) {
                continue;
            }
            ultimas.merge(membresia.getCliente().getId(), membresia, this::masReciente);
        }
        return ultimas;
    }

    private MembresiaCliente masReciente(MembresiaCliente actual, MembresiaCliente candidata) {
        LocalDate fechaActual = actual.getFechaVencimiento();
        LocalDate fechaCandidata = candidata.getFechaVencimiento();
        if (fechaActual == null && fechaCandidata == null) {
            return idMayor(actual, candidata);
        }
        if (fechaActual == null) {
            return candidata;
        }
        if (fechaCandidata == null) {
            return actual;
        }
        int comparacion = fechaActual.compareTo(fechaCandidata);
        return comparacion == 0 ? idMayor(actual, candidata) : comparacion > 0 ? actual : candidata;
    }

    private MembresiaCliente idMayor(MembresiaCliente a, MembresiaCliente b) {
        if (a.getId() == null) {
            return b;
        }
        if (b.getId() == null) {
            return a;
        }
        return a.getId() >= b.getId() ? a : b;
    }

    private boolean enMes(LocalDateTime fecha, YearMonth mes) {
        return fecha != null && YearMonth.from(fecha).equals(mes);
    }
}
