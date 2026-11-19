package system_gym.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Cliente;
import system_gym.model.Falla;
import system_gym.model.FichaEntrenamiento;
import system_gym.model.FichaMedica;
import system_gym.model.MembresiaCliente;
import system_gym.model.Pago;
import system_gym.model.PlanesMembresia;
import system_gym.model.RegistroAuditoria;
import system_gym.model.Usuario;
import system_gym.reporte.EstadoMembresia;
import system_gym.reporte.EstadoMembresiaUtil;
import system_gym.reporte.ReporteColumna;
import system_gym.reporte.ReporteData;
import system_gym.reporte.ReporteFiltro;
import system_gym.reporte.ReporteFormatoUtil;
import system_gym.reporte.ReporteTipo;
import system_gym.reporte.TipoDato;
import system_gym.repository.ClienteRepository;
import system_gym.repository.FallaRepository;
import system_gym.repository.FichaEntrenamientoRepository;
import system_gym.repository.FichaMedicaRepository;
import system_gym.repository.MembresiaClienteRepository;
import system_gym.repository.PagoRepository;
import system_gym.repository.RegistroAuditoriaRepository;

/**
 * Construye los datos de cada reporte aplicando filtros y resúmenes.
 */
@Service
@Transactional(readOnly = true)
public class ReporteService {

    private static final List<ReporteColumna> COLUMNAS_MIEMBROS = List.of(
            new ReporteColumna("id", "ID", TipoDato.ENTERO, 8),
            new ReporteColumna("nombre", "Nombre", TipoDato.TEXTO, 18),
            new ReporteColumna("apellido", "Apellido", TipoDato.TEXTO, 18),
            new ReporteColumna("documento", "Documento", TipoDato.TEXTO, 14),
            new ReporteColumna("telefono", "Teléfono", TipoDato.TEXTO, 14),
            new ReporteColumna("correo", "Correo", TipoDato.TEXTO, 24),
            new ReporteColumna("ciudad", "Ciudad", TipoDato.TEXTO, 16),
            new ReporteColumna("sexo", "Sexo", TipoDato.TEXTO, 10),
            new ReporteColumna("fechaRegistro", "Registro", TipoDato.FECHA_HORA, 16),
            new ReporteColumna("estadoMembresia", "Estado membresía", TipoDato.TEXTO, 14),
            new ReporteColumna("fechaVencimiento", "Vencimiento", TipoDato.FECHA, 12));

    private static final List<ReporteColumna> COLUMNAS_MEMBRESIAS = List.of(
            new ReporteColumna("id", "ID", TipoDato.ENTERO, 8),
            new ReporteColumna("cliente", "Cliente", TipoDato.TEXTO, 26),
            new ReporteColumna("documento", "Documento", TipoDato.TEXTO, 14),
            new ReporteColumna("planes", "Planes", TipoDato.TEXTO, 30),
            new ReporteColumna("fechaInicio", "Inicio", TipoDato.FECHA, 12),
            new ReporteColumna("fechaVencimiento", "Vencimiento", TipoDato.FECHA, 12),
            new ReporteColumna("estado", "Estado", TipoDato.TEXTO, 12),
            new ReporteColumna("precio", "Precio", TipoDato.MONEDA, 14),
            new ReporteColumna("renovacion", "Renovación", TipoDato.BOOLEANO, 10));

    private static final List<ReporteColumna> COLUMNAS_INGRESOS = List.of(
            new ReporteColumna("id", "ID", TipoDato.ENTERO, 8),
            new ReporteColumna("fechaPago", "Fecha pago", TipoDato.FECHA_HORA, 16),
            new ReporteColumna("cliente", "Cliente", TipoDato.TEXTO, 26),
            new ReporteColumna("documento", "Documento", TipoDato.TEXTO, 14),
            new ReporteColumna("membresia", "Membresía", TipoDato.ENTERO, 10),
            new ReporteColumna("metodo", "Método", TipoDato.TEXTO, 14),
            new ReporteColumna("monto", "Monto", TipoDato.MONEDA, 14),
            new ReporteColumna("descuento", "Descuento", TipoDato.MONEDA, 12),
            new ReporteColumna("estado", "Estado", TipoDato.TEXTO, 12),
            new ReporteColumna("vencimiento", "Vencimiento", TipoDato.FECHA, 12));

    private static final List<ReporteColumna> COLUMNAS_MOROSIDAD = List.of(
            new ReporteColumna("cliente", "Cliente", TipoDato.TEXTO, 26),
            new ReporteColumna("documento", "Documento", TipoDato.TEXTO, 14),
            new ReporteColumna("telefono", "Teléfono", TipoDato.TEXTO, 14),
            new ReporteColumna("membresia", "Membresía", TipoDato.ENTERO, 10),
            new ReporteColumna("vencimiento", "Vencimiento", TipoDato.FECHA, 12),
            new ReporteColumna("diasVencidos", "Días vencidos", TipoDato.ENTERO, 10),
            new ReporteColumna("monto", "Monto", TipoDato.MONEDA, 14),
            new ReporteColumna("estado", "Estado", TipoDato.TEXTO, 12));

    private static final List<ReporteColumna> COLUMNAS_ENTRENAMIENTOS = List.of(
            new ReporteColumna("id", "ID", TipoDato.ENTERO, 8),
            new ReporteColumna("fecha", "Fecha", TipoDato.FECHA_HORA, 16),
            new ReporteColumna("cliente", "Cliente", TipoDato.TEXTO, 26),
            new ReporteColumna("documento", "Documento", TipoDato.TEXTO, 14),
            new ReporteColumna("objetivo", "Objetivo", TipoDato.TEXTO, 30),
            new ReporteColumna("dias", "Días", TipoDato.TEXTO, 30),
            new ReporteColumna("entrenadores", "Entrenadores", TipoDato.TEXTO, 30),
            new ReporteColumna("membresia", "Membresía", TipoDato.ENTERO, 10));

    private static final List<ReporteColumna> COLUMNAS_EVALUACIONES = List.of(
            new ReporteColumna("id", "ID", TipoDato.ENTERO, 8),
            new ReporteColumna("fecha", "Fecha", TipoDato.FECHA_HORA, 16),
            new ReporteColumna("cliente", "Cliente", TipoDato.TEXTO, 26),
            new ReporteColumna("documento", "Documento", TipoDato.TEXTO, 14),
            new ReporteColumna("entrenador", "Entrenador", TipoDato.TEXTO, 18),
            new ReporteColumna("peso", "Peso", TipoDato.DECIMAL, 8),
            new ReporteColumna("altura", "Altura", TipoDato.DECIMAL, 8),
            new ReporteColumna("imc", "IMC", TipoDato.DECIMAL, 8),
            new ReporteColumna("grasa", "% Grasa", TipoDato.DECIMAL, 10),
            new ReporteColumna("masa", "Masa corporal", TipoDato.DECIMAL, 10),
            new ReporteColumna("presion", "Presión", TipoDato.DECIMAL, 10),
            new ReporteColumna("frecuencia", "Frec. cardíaca", TipoDato.ENTERO, 10),
            new ReporteColumna("apto", "Apto", TipoDato.BOOLEANO, 8));

    private static final List<ReporteColumna> COLUMNAS_COMPARATIVA = List.of(
            new ReporteColumna("cliente", "Cliente", TipoDato.TEXTO, 26),
            new ReporteColumna("documento", "Documento", TipoDato.TEXTO, 14),
            new ReporteColumna("evaluaciones", "Evaluaciones", TipoDato.ENTERO, 10),
            new ReporteColumna("primeraFecha", "Primera", TipoDato.FECHA, 12),
            new ReporteColumna("ultimaFecha", "Última", TipoDato.FECHA, 12),
            new ReporteColumna("pesoInicial", "Peso inicial", TipoDato.DECIMAL, 10),
            new ReporteColumna("pesoFinal", "Peso final", TipoDato.DECIMAL, 10),
            new ReporteColumna("variacionPeso", "Var. peso", TipoDato.DECIMAL, 10),
            new ReporteColumna("imcInicial", "IMC inicial", TipoDato.DECIMAL, 8),
            new ReporteColumna("imcFinal", "IMC final", TipoDato.DECIMAL, 8),
            new ReporteColumna("variacionImc", "Var. IMC", TipoDato.DECIMAL, 10),
            new ReporteColumna("grasaInicial", "Grasa inicial", TipoDato.DECIMAL, 10),
            new ReporteColumna("grasaFinal", "Grasa final", TipoDato.DECIMAL, 10),
            new ReporteColumna("variacionGrasa", "Var. grasa", TipoDato.DECIMAL, 10));

    private static final List<ReporteColumna> COLUMNAS_FALLAS = List.of(
            new ReporteColumna("id", "ID", TipoDato.ENTERO, 8),
            new ReporteColumna("fecha", "Fecha", TipoDato.FECHA_HORA, 16),
            new ReporteColumna("reportadoPor", "Reportado por", TipoDato.TEXTO, 18),
            new ReporteColumna("area", "Área / equipo", TipoDato.TEXTO, 20),
            new ReporteColumna("prioridad", "Prioridad", TipoDato.TEXTO, 10),
            new ReporteColumna("estado", "Estado", TipoDato.TEXTO, 12),
            new ReporteColumna("responsable", "Responsable", TipoDato.TEXTO, 18),
            new ReporteColumna("descripcion", "Descripción", TipoDato.TEXTO, 40),
            new ReporteColumna("fechaCierre", "Cierre", TipoDato.FECHA_HORA, 16),
            new ReporteColumna("observacion", "Observación", TipoDato.TEXTO, 30));

    private static final List<ReporteColumna> COLUMNAS_AUDITORIA = List.of(
            new ReporteColumna("fecha", "Fecha", TipoDato.FECHA_HORA, 16),
            new ReporteColumna("usuario", "Usuario", TipoDato.TEXTO, 16),
            new ReporteColumna("rol", "Rol", TipoDato.TEXTO, 14),
            new ReporteColumna("accion", "Acción", TipoDato.TEXTO, 12),
            new ReporteColumna("modulo", "Módulo", TipoDato.TEXTO, 14),
            new ReporteColumna("metodo", "Método", TipoDato.TEXTO, 8),
            new ReporteColumna("ruta", "Ruta", TipoDato.TEXTO, 30),
            new ReporteColumna("ip", "IP", TipoDato.TEXTO, 14),
            new ReporteColumna("codigo", "Código", TipoDato.ENTERO, 8),
            new ReporteColumna("resultado", "Resultado", TipoDato.TEXTO, 10),
            new ReporteColumna("duracion", "Duración ms", TipoDato.ENTERO, 10),
            new ReporteColumna("descripcion", "Descripción", TipoDato.TEXTO, 30));

    private final ClienteRepository clienteRepository;
    private final MembresiaClienteRepository membresiaRepository;
    private final PagoRepository pagoRepository;
    private final FichaEntrenamientoRepository fichaEntrenamientoRepository;
    private final FichaMedicaRepository fichaMedicaRepository;
    private final FallaRepository fallaRepository;
    private final RegistroAuditoriaRepository auditoriaRepository;
    private final ConfiguracionService configuracionService;

    public ReporteService(ClienteRepository clienteRepository,
            MembresiaClienteRepository membresiaRepository,
            PagoRepository pagoRepository,
            FichaEntrenamientoRepository fichaEntrenamientoRepository,
            FichaMedicaRepository fichaMedicaRepository,
            FallaRepository fallaRepository,
            RegistroAuditoriaRepository auditoriaRepository,
            ConfiguracionService configuracionService) {
        this.clienteRepository = clienteRepository;
        this.membresiaRepository = membresiaRepository;
        this.pagoRepository = pagoRepository;
        this.fichaEntrenamientoRepository = fichaEntrenamientoRepository;
        this.fichaMedicaRepository = fichaMedicaRepository;
        this.fallaRepository = fallaRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.configuracionService = configuracionService;
    }

    public ReporteData generar(ReporteTipo tipo, ReporteFiltro filtro) {
        LocalDate hoy = LocalDate.now();
        return switch (tipo) {
            case MIEMBROS -> generarMiembros(filtro, hoy);
            case MEMBRESIAS -> generarMembresias(filtro, hoy);
            case INGRESOS -> generarIngresos(filtro);
            case MOROSIDAD -> generarMorosidad(filtro, hoy);
            case ENTRENAMIENTOS -> generarEntrenamientos(filtro);
            case EVALUACIONES -> generarEvaluaciones(filtro);
            case COMPARATIVA -> generarComparativa(filtro);
            case FALLAS -> generarFallas(filtro);
            case AUDITORIA -> generarAuditoria(filtro);
        };
    }

    public ReporteData generarPersonalizado(String slug, ReporteFiltro filtro) {
        ReporteTipo tipo = ReporteTipo.desdeSlug(slug).orElse(ReporteTipo.MIEMBROS);
        if (!esPersonalizable(tipo)) {
            tipo = ReporteTipo.MIEMBROS;
        }
        ReporteData base = generar(tipo, filtro);
        List<ReporteColumna> seleccionadas = base.columnas().stream()
                .filter(columna -> filtro.getColumnas().isEmpty()
                        || filtro.getColumnas().contains(columna.clave()))
                .toList();
        if (seleccionadas.isEmpty()) {
            seleccionadas = base.columnas();
        }
        List<Map<String, Object>> filas = new ArrayList<>();
        for (Map<String, Object> fila : base.filas()) {
            Map<String, Object> nueva = new LinkedHashMap<>();
            for (ReporteColumna columna : seleccionadas) {
                nueva.put(columna.clave(), fila.get(columna.clave()));
            }
            filas.add(nueva);
        }
        return new ReporteData(base.gimnasio(), "Reporte personalizado - " + tipo.getTitulo(),
                base.subtitulo(), seleccionadas, filas, base.resumen(), base.generadoEn());
    }

    public List<ReporteColumna> columnasDisponibles(String slug) {
        ReporteTipo tipo = ReporteTipo.desdeSlug(slug).orElse(ReporteTipo.MIEMBROS);
        return esPersonalizable(tipo) ? columnasDe(tipo) : List.of();
    }

    public List<ReporteTipo> tiposPersonalizables() {
        return List.of(ReporteTipo.MIEMBROS, ReporteTipo.MEMBRESIAS, ReporteTipo.INGRESOS,
                ReporteTipo.MOROSIDAD, ReporteTipo.ENTRENAMIENTOS, ReporteTipo.EVALUACIONES,
                ReporteTipo.FALLAS);
    }

    private ReporteData generarMiembros(ReporteFiltro filtro, LocalDate hoy) {
        Map<Integer, MembresiaCliente> ultimas = ultimaMembresiaPorCliente(membresiaRepository.findAll());
        List<Map<String, Object>> filas = new ArrayList<>();
        int activos = 0;
        int vencidos = 0;
        int congelados = 0;

        List<Cliente> clientes = clienteRepository.findAll().stream()
                .filter(cliente -> filtro.getClienteId() == null
                        || cliente.getId().equals(filtro.getClienteId()))
                .filter(cliente -> dentro(cliente.getFechaRegistro(), filtro.getFechaDesde(), filtro.getFechaHasta()))
                .filter(cliente -> coincideTexto(filtro.getTexto(), cliente.getNombre(), cliente.getApellido(),
                        cliente.getDocumento(), cliente.getCorreo(), cliente.getTelefono()))
                .sorted(Comparator.comparing(Cliente::getApellido, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Cliente::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();

        for (Cliente cliente : clientes) {
            MembresiaCliente membresia = ultimas.get(cliente.getId());
            EstadoMembresia estado = EstadoMembresiaUtil.calcular(membresia, hoy);
            if (estado == EstadoMembresia.ACTIVA) {
                activos++;
            } else if (estado == EstadoMembresia.VENCIDA) {
                vencidos++;
            } else if (estado == EstadoMembresia.CONGELADA) {
                congelados++;
            }
            filas.add(fila(
                    "id", cliente.getId(),
                    "nombre", cliente.getNombre(),
                    "apellido", cliente.getApellido(),
                    "documento", cliente.getDocumento(),
                    "telefono", cliente.getTelefono(),
                    "correo", cliente.getCorreo(),
                    "ciudad", cliente.getCiudad(),
                    "sexo", sexoTexto(cliente.getSexo()),
                    "fechaRegistro", cliente.getFechaRegistro(),
                    "estadoMembresia", estado.getTexto(),
                    "fechaVencimiento", membresia == null ? null : membresia.getFechaVencimiento()));
        }

        List<String> resumen = List.of(
                "Total de miembros: " + filas.size(),
                "Activos: " + activos,
                "Vencidos: " + vencidos,
                "Congelados: " + congelados);
        return reporte(ReporteTipo.MIEMBROS, filtro, COLUMNAS_MIEMBROS, filas, resumen, null);
    }

    private ReporteData generarMembresias(ReporteFiltro filtro, LocalDate hoy) {
        List<Map<String, Object>> filas = new ArrayList<>();
        int activas = 0;
        int vencidas = 0;
        int congeladas = 0;
        long total = 0L;

        List<MembresiaCliente> membresias = membresiaRepository.findAll().stream()
                .filter(membresia -> filtro.getClienteId() == null
                        || (membresia.getCliente() != null
                                && membresia.getCliente().getId().equals(filtro.getClienteId())))
                .filter(membresia -> dentro(membresia.getFechaInicio(), filtro.getFechaDesde(), filtro.getFechaHasta()))
                .filter(membresia -> coincideEstado(EstadoMembresiaUtil.calcular(membresia, hoy), filtro.getEstado()))
                .filter(membresia -> coincideTexto(filtro.getTexto(), nombreCliente(membresia.getCliente()),
                        membresia.getCliente() == null ? null : membresia.getCliente().getDocumento(),
                        planesTexto(membresia.getPlanes())))
                .sorted(Comparator.comparing(MembresiaCliente::getFechaVencimiento,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(membresia -> nombreCliente(membresia.getCliente())))
                .toList();

        for (MembresiaCliente membresia : membresias) {
            EstadoMembresia estado = EstadoMembresiaUtil.calcular(membresia, hoy);
            if (estado == EstadoMembresia.ACTIVA) {
                activas++;
            } else if (estado == EstadoMembresia.VENCIDA) {
                vencidas++;
            } else if (estado == EstadoMembresia.CONGELADA) {
                congeladas++;
            }
            total += membresia.getPrecioAPagar() == null ? 0 : membresia.getPrecioAPagar();
            filas.add(fila(
                    "id", membresia.getId(),
                    "cliente", nombreCliente(membresia.getCliente()),
                    "documento", membresia.getCliente() == null ? null : membresia.getCliente().getDocumento(),
                    "planes", planesTexto(membresia.getPlanes()),
                    "fechaInicio", membresia.getFechaInicio(),
                    "fechaVencimiento", membresia.getFechaVencimiento(),
                    "estado", estado.getTexto(),
                    "precio", membresia.getPrecioAPagar(),
                    "renovacion", membresia.getRenovacionAutomatica()));
        }

        List<String> resumen = List.of(
                "Total de membresías: " + filas.size(),
                "Activas: " + activas,
                "Vencidas: " + vencidas,
                "Congeladas: " + congeladas,
                "Precio total: " + ReporteFormatoUtil.moneda(total));
        return reporte(ReporteTipo.MEMBRESIAS, filtro, COLUMNAS_MEMBRESIAS, filas, resumen, null);
    }

    private ReporteData generarIngresos(ReporteFiltro filtro) {
        Short estado = filtro.getEstado() == null ? (short) 2 : filtro.getEstado();
        List<Map<String, Object>> filas = new ArrayList<>();
        long total = 0L;
        long efectivo = 0L;
        long tarjeta = 0L;
        long transferencia = 0L;

        List<Pago> pagos = pagoRepository.findAll().stream()
                .filter(pago -> pago.getEstadoPago() != null && pago.getEstadoPago().equals(estado))
                .filter(pago -> filtro.getClienteId() == null
                        || (pago.getCliente() != null && pago.getCliente().getId().equals(filtro.getClienteId())))
                .filter(pago -> filtro.getMetodoPago() == null
                        || Objects.equals(pago.getMetodoPago(), filtro.getMetodoPago()))
                .filter(pago -> dentro(pago.getFechaPago(), filtro.getFechaDesde(), filtro.getFechaHasta()))
                .filter(pago -> coincideTexto(filtro.getTexto(), nombreCliente(pago.getCliente()),
                        pago.getCliente() == null ? null : pago.getCliente().getDocumento(),
                        metodoPagoTexto(pago.getMetodoPago())))
                .sorted(Comparator.comparing(Pago::getFechaPago, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        for (Pago pago : pagos) {
            long monto = pago.getMontoTotal() == null ? 0 : pago.getMontoTotal();
            total += monto;
            if (pago.getMetodoPago() != null) {
                if (pago.getMetodoPago() == 1) {
                    efectivo += monto;
                } else if (pago.getMetodoPago() == 2) {
                    tarjeta += monto;
                } else if (pago.getMetodoPago() == 3) {
                    transferencia += monto;
                }
            }
            filas.add(fila(
                    "id", pago.getId(),
                    "fechaPago", pago.getFechaPago(),
                    "cliente", nombreCliente(pago.getCliente()),
                    "documento", pago.getCliente() == null ? null : pago.getCliente().getDocumento(),
                    "membresia", pago.getMembresia() == null ? null : pago.getMembresia().getId(),
                    "metodo", metodoPagoTexto(pago.getMetodoPago()),
                    "monto", pago.getMontoTotal(),
                    "descuento", pago.getDescuentoAplicado(),
                    "estado", estadoPagoTexto(pago.getEstadoPago()),
                    "vencimiento", pago.getFechaVencimiento()));
        }

        List<String> resumen = List.of(
                "Pagos incluidos: " + filas.size(),
                "Total: " + ReporteFormatoUtil.moneda(total),
                "Efectivo: " + ReporteFormatoUtil.moneda(efectivo),
                "Tarjeta: " + ReporteFormatoUtil.moneda(tarjeta),
                "Transferencia: " + ReporteFormatoUtil.moneda(transferencia));
        return reporte(ReporteTipo.INGRESOS, filtro, COLUMNAS_INGRESOS, filas, resumen,
                estadoPagoTexto(estado));
    }

    private ReporteData generarMorosidad(ReporteFiltro filtro, LocalDate hoy) {
        List<Map<String, Object>> filas = new ArrayList<>();
        long total = 0L;
        long diasAcumulados = 0L;

        List<Pago> pagos = pagoRepository.findAll().stream()
                .filter(pago -> pago.getEstadoPago() != null && pago.getEstadoPago() == 1)
                .filter(pago -> pago.getFechaVencimiento() != null
                        && pago.getFechaVencimiento().isBefore(hoy))
                .filter(pago -> filtro.getClienteId() == null
                        || (pago.getCliente() != null && pago.getCliente().getId().equals(filtro.getClienteId())))
                .filter(pago -> dentro(pago.getFechaVencimiento(), filtro.getFechaDesde(), filtro.getFechaHasta()))
                .filter(pago -> coincideTexto(filtro.getTexto(), nombreCliente(pago.getCliente()),
                        pago.getCliente() == null ? null : pago.getCliente().getDocumento(),
                        pago.getCliente() == null ? null : pago.getCliente().getTelefono()))
                .sorted(Comparator.comparing((Pago pago) -> pago.getFechaVencimiento(),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        for (Pago pago : pagos) {
            long dias = ChronoUnit.DAYS.between(pago.getFechaVencimiento(), hoy);
            long monto = pago.getMontoTotal() == null ? 0 : pago.getMontoTotal();
            total += monto;
            diasAcumulados += dias;
            filas.add(fila(
                    "cliente", nombreCliente(pago.getCliente()),
                    "documento", pago.getCliente() == null ? null : pago.getCliente().getDocumento(),
                    "telefono", pago.getCliente() == null ? null : pago.getCliente().getTelefono(),
                    "membresia", pago.getMembresia() == null ? null : pago.getMembresia().getId(),
                    "vencimiento", pago.getFechaVencimiento(),
                    "diasVencidos", dias,
                    "monto", pago.getMontoTotal(),
                    "estado", estadoPagoTexto(pago.getEstadoPago())));
        }

        long promedioDias = filas.isEmpty() ? 0 : diasAcumulados / filas.size();
        List<String> resumen = List.of(
                "Pagos vencidos: " + filas.size(),
                "Monto adeudado: " + ReporteFormatoUtil.moneda(total),
                "Promedio de días vencidos: " + promedioDias);
        return reporte(ReporteTipo.MOROSIDAD, filtro, COLUMNAS_MOROSIDAD, filas, resumen,
                "Corte al " + ReporteFormatoUtil.fecha(hoy));
    }

    private ReporteData generarEntrenamientos(ReporteFiltro filtro) {
        List<Map<String, Object>> filas = new ArrayList<>();
        Set<Integer> clientes = new HashSet<>();
        Set<Integer> entrenadores = new HashSet<>();

        List<FichaEntrenamiento> fichas = fichaEntrenamientoRepository.findAll().stream()
                .filter(ficha -> filtro.getClienteId() == null
                        || (ficha.getCliente() != null && ficha.getCliente().getId().equals(filtro.getClienteId())))
                .filter(ficha -> filtro.getResponsableId() == null
                        || ficha.getEntrenadores().stream()
                                .anyMatch(usuario -> usuario.getId().equals(filtro.getResponsableId())))
                .filter(ficha -> dentro(ficha.getFechaCreacion(), filtro.getFechaDesde(), filtro.getFechaHasta()))
                .filter(ficha -> coincideTexto(filtro.getTexto(), ficha.getObjetivo(),
                        nombreCliente(ficha.getCliente()),
                        ficha.getCliente() == null ? null : ficha.getCliente().getDocumento(),
                        entrenadoresTexto(ficha.getEntrenadores())))
                .sorted(Comparator.comparing(FichaEntrenamiento::getFechaCreacion,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        for (FichaEntrenamiento ficha : fichas) {
            if (ficha.getCliente() != null) {
                clientes.add(ficha.getCliente().getId());
            }
            ficha.getEntrenadores().forEach(usuario -> entrenadores.add(usuario.getId()));
            filas.add(fila(
                    "id", ficha.getId(),
                    "fecha", ficha.getFechaCreacion(),
                    "cliente", nombreCliente(ficha.getCliente()),
                    "documento", ficha.getCliente() == null ? null : ficha.getCliente().getDocumento(),
                    "objetivo", ficha.getObjetivo(),
                    "dias", ficha.getDiasSemanaTexto(),
                    "entrenadores", entrenadoresTexto(ficha.getEntrenadores()),
                    "membresia", ficha.getMembresia() == null ? null : ficha.getMembresia().getId()));
        }

        List<String> resumen = List.of(
                "Total de fichas: " + filas.size(),
                "Clientes atendidos: " + clientes.size(),
                "Entrenadores involucrados: " + entrenadores.size());
        return reporte(ReporteTipo.ENTRENAMIENTOS, filtro, COLUMNAS_ENTRENAMIENTOS, filas, resumen, null);
    }

    private ReporteData generarEvaluaciones(ReporteFiltro filtro) {
        List<Map<String, Object>> filas = new ArrayList<>();
        List<BigDecimal> imcs = new ArrayList<>();
        List<BigDecimal> grasas = new ArrayList<>();
        int aptos = 0;

        List<FichaMedica> fichas = fichaMedicaRepository.findAll().stream()
                .filter(ficha -> filtro.getClienteId() == null
                        || (ficha.getCliente() != null && ficha.getCliente().getId().equals(filtro.getClienteId())))
                .filter(ficha -> filtro.getResponsableId() == null
                        || (ficha.getEntrenador() != null
                                && ficha.getEntrenador().getId().equals(filtro.getResponsableId())))
                .filter(ficha -> dentro(ficha.getFecha(), filtro.getFechaDesde(), filtro.getFechaHasta()))
                .filter(ficha -> coincideTexto(filtro.getTexto(), nombreCliente(ficha.getCliente()),
                        ficha.getCliente() == null ? null : ficha.getCliente().getDocumento(),
                        ficha.getEntrenador() == null ? null : ficha.getEntrenador().getUsuario()))
                .sorted(Comparator.comparing(FichaMedica::getFecha,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        for (FichaMedica ficha : fichas) {
            if (Boolean.TRUE.equals(ficha.getAptoEntrenar())) {
                aptos++;
            }
            if (ficha.getImc() != null) {
                imcs.add(ficha.getImc());
            }
            if (ficha.getPorcentajeGrasa() != null) {
                grasas.add(ficha.getPorcentajeGrasa());
            }
            filas.add(fila(
                    "id", ficha.getId(),
                    "fecha", ficha.getFecha(),
                    "cliente", nombreCliente(ficha.getCliente()),
                    "documento", ficha.getCliente() == null ? null : ficha.getCliente().getDocumento(),
                    "entrenador", ficha.getEntrenador() == null ? null : ficha.getEntrenador().getUsuario(),
                    "peso", ficha.getPeso(),
                    "altura", ficha.getAltura(),
                    "imc", ficha.getImc(),
                    "grasa", ficha.getPorcentajeGrasa(),
                    "masa", ficha.getMasaCorporal(),
                    "presion", ficha.getPresionArterial(),
                    "frecuencia", ficha.getFrecuenciaCardiaca(),
                    "apto", ficha.getAptoEntrenar()));
        }

        List<String> resumen = List.of(
                "Total de evaluaciones: " + filas.size(),
                "Aptos: " + aptos,
                "No aptos: " + (filas.size() - aptos),
                "IMC promedio: " + ReporteFormatoUtil.decimal(promedio(imcs)),
                "% Grasa promedio: " + ReporteFormatoUtil.decimal(promedio(grasas)));
        return reporte(ReporteTipo.EVALUACIONES, filtro, COLUMNAS_EVALUACIONES, filas, resumen, null);
    }

    private ReporteData generarComparativa(ReporteFiltro filtro) {
        Map<Integer, List<FichaMedica>> porCliente = fichaMedicaRepository.findAll().stream()
                .filter(ficha -> ficha.getCliente() != null)
                .filter(ficha -> filtro.getClienteId() == null
                        || ficha.getCliente().getId().equals(filtro.getClienteId()))
                .filter(ficha -> dentro(ficha.getFecha(), filtro.getFechaDesde(), filtro.getFechaHasta()))
                .filter(ficha -> coincideTexto(filtro.getTexto(), nombreCliente(ficha.getCliente()),
                        ficha.getCliente().getDocumento()))
                .collect(Collectors.groupingBy(ficha -> ficha.getCliente().getId()));

        List<Map<String, Object>> filas = new ArrayList<>();
        int mejoranImc = 0;
        int aumentanGrasa = 0;

        for (List<FichaMedica> evaluaciones : porCliente.values()) {
            if (evaluaciones.size() < 2) {
                continue;
            }
            evaluaciones.sort(Comparator.comparing(FichaMedica::getFecha));
            FichaMedica primera = evaluaciones.get(0);
            FichaMedica ultima = evaluaciones.get(evaluaciones.size() - 1);
            BigDecimal variacionPeso = diferencia(ultima.getPeso(), primera.getPeso());
            BigDecimal variacionImc = diferencia(ultima.getImc(), primera.getImc());
            BigDecimal variacionGrasa = diferencia(ultima.getPorcentajeGrasa(), primera.getPorcentajeGrasa());
            if (variacionImc != null && variacionImc.compareTo(BigDecimal.ZERO) < 0) {
                mejoranImc++;
            }
            if (variacionGrasa != null && variacionGrasa.compareTo(BigDecimal.ZERO) > 0) {
                aumentanGrasa++;
            }
            filas.add(fila(
                    "cliente", nombreCliente(primera.getCliente()),
                    "documento", primera.getCliente().getDocumento(),
                    "evaluaciones", evaluaciones.size(),
                    "primeraFecha", primera.getFecha(),
                    "ultimaFecha", ultima.getFecha(),
                    "pesoInicial", primera.getPeso(),
                    "pesoFinal", ultima.getPeso(),
                    "variacionPeso", variacionPeso,
                    "imcInicial", primera.getImc(),
                    "imcFinal", ultima.getImc(),
                    "variacionImc", variacionImc,
                    "grasaInicial", primera.getPorcentajeGrasa(),
                    "grasaFinal", ultima.getPorcentajeGrasa(),
                    "variacionGrasa", variacionGrasa));
        }

        filas.sort(Comparator.comparing(fila -> String.valueOf(fila.get("cliente"))));
        List<String> resumen = List.of(
                "Clientes comparados: " + filas.size(),
                "Con mejora de IMC: " + mejoranImc,
                "Con aumento de grasa: " + aumentanGrasa);
        return reporte(ReporteTipo.COMPARATIVA, filtro, COLUMNAS_COMPARATIVA, filas, resumen, null);
    }

    private ReporteData generarFallas(ReporteFiltro filtro) {
        List<Map<String, Object>> filas = new ArrayList<>();
        int abiertas = 0;
        int enProceso = 0;
        int resueltas = 0;
        int altaPrioridad = 0;

        List<Falla> fallas = fallaRepository.findAll().stream()
                .filter(falla -> filtro.getEstado() == null
                        || Objects.equals(falla.getEstado(), filtro.getEstado()))
                .filter(falla -> filtro.getPrioridad() == null
                        || Objects.equals(falla.getPrioridad(), filtro.getPrioridad()))
                .filter(falla -> filtro.getResponsableId() == null
                        || (falla.getResponsableAtencion() != null
                                && falla.getResponsableAtencion().getId().equals(filtro.getResponsableId())))
                .filter(falla -> dentro(falla.getFechaReporte(), filtro.getFechaDesde(), filtro.getFechaHasta()))
                .filter(falla -> coincideTexto(filtro.getTexto(), falla.getDescripcion(), falla.getAreaEquipo(),
                        falla.getReportadoPor(),
                        falla.getResponsableAtencion() == null ? null : falla.getResponsableAtencion().getUsuario()))
                .sorted(Comparator.comparing(Falla::getFechaReporte,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        for (Falla falla : fallas) {
            if (falla.getEstado() != null) {
                if (falla.getEstado() == 1) {
                    abiertas++;
                } else if (falla.getEstado() == 2) {
                    enProceso++;
                } else if (falla.getEstado() == 3) {
                    resueltas++;
                }
            }
            if (falla.getPrioridad() != null && falla.getPrioridad() == 3) {
                altaPrioridad++;
            }
            filas.add(fila(
                    "id", falla.getId(),
                    "fecha", falla.getFechaReporte(),
                    "reportadoPor", falla.getReportadoPor(),
                    "area", falla.getAreaEquipo(),
                    "prioridad", falla.getPrioridadTexto(),
                    "estado", falla.getEstadoTexto(),
                    "responsable", falla.getResponsableAtencion() == null
                            ? "Sin asignar" : falla.getResponsableAtencion().getUsuario(),
                    "descripcion", falla.getDescripcion(),
                    "fechaCierre", falla.getFechaCierre(),
                    "observacion", falla.getObservacion()));
        }

        List<String> resumen = List.of(
                "Total de fallas: " + filas.size(),
                "Abiertas: " + abiertas,
                "En proceso: " + enProceso,
                "Resueltas: " + resueltas,
                "Prioridad alta: " + altaPrioridad);
        return reporte(ReporteTipo.FALLAS, filtro, COLUMNAS_FALLAS, filas, resumen, null);
    }

    private ReporteData generarAuditoria(ReporteFiltro filtro) {
        List<Map<String, Object>> filas = new ArrayList<>();
        Set<String> usuarios = new HashSet<>();
        int errores = 0;

        List<RegistroAuditoria> registros = auditoriaRepository.findAll().stream()
                .filter(registro -> dentro(registro.getFechaHora(), filtro.getFechaDesde(), filtro.getFechaHasta()))
                .filter(registro -> coincideTexto(filtro.getTexto(), registro.getUsuario(), registro.getRol(),
                        registro.getAccion(), registro.getModulo(), registro.getRuta(), registro.getIp(),
                        registro.getDescripcion()))
                .sorted(Comparator.comparing(RegistroAuditoria::getFechaHora,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        for (RegistroAuditoria registro : registros) {
            if (registro.getUsuario() != null) {
                usuarios.add(registro.getUsuario());
            }
            if ("ERROR".equalsIgnoreCase(registro.getResultado())) {
                errores++;
            }
            filas.add(fila(
                    "fecha", registro.getFechaHora(),
                    "usuario", registro.getUsuario(),
                    "rol", registro.getRol(),
                    "accion", registro.getAccion(),
                    "modulo", registro.getModulo(),
                    "metodo", registro.getMetodoHttp(),
                    "ruta", registro.getRuta(),
                    "ip", registro.getIp(),
                    "codigo", registro.getCodigoHttp(),
                    "resultado", registro.getResultado(),
                    "duracion", registro.getDuracionMs(),
                    "descripcion", registro.getDescripcion()));
        }

        List<String> resumen = List.of(
                "Total de registros: " + filas.size(),
                "Errores: " + errores,
                "Usuarios únicos: " + usuarios.size());
        return reporte(ReporteTipo.AUDITORIA, filtro, COLUMNAS_AUDITORIA, filas, resumen, null);
    }

    private ReporteData reporte(ReporteTipo tipo, ReporteFiltro filtro, List<ReporteColumna> columnas,
            List<Map<String, Object>> filas, List<String> resumen, String detalle) {
        String gimnasio = configuracionService.obtenerConfiguracion().getNombre();
        if (gimnasio == null || gimnasio.isBlank()) {
            gimnasio = "Sistema Gym";
        }
        return new ReporteData(gimnasio, tipo.getTitulo(), subtitulo(filtro, detalle), columnas, filas,
                resumen, LocalDateTime.now());
    }

    private String subtitulo(ReporteFiltro filtro, String detalle) {
        List<String> partes = new ArrayList<>();
        if (filtro.getFechaDesde() != null || filtro.getFechaHasta() != null) {
            String desde = filtro.getFechaDesde() == null ? "inicio"
                    : ReporteFormatoUtil.fecha(filtro.getFechaDesde());
            String hasta = filtro.getFechaHasta() == null ? "hoy"
                    : ReporteFormatoUtil.fecha(filtro.getFechaHasta());
            partes.add("Periodo: " + desde + " a " + hasta);
        }
        if (filtro.getTexto() != null && !filtro.getTexto().isBlank()) {
            partes.add("Búsqueda: " + filtro.getTexto().trim());
        }
        if (detalle != null && !detalle.isBlank()) {
            partes.add(detalle);
        }
        return partes.isEmpty() ? "Sin filtros aplicados" : String.join(" | ", partes);
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

    private boolean coincideEstado(EstadoMembresia estado, Short filtroEstado) {
        if (filtroEstado == null) {
            return true;
        }
        return switch (filtroEstado) {
            case 1 -> estado == EstadoMembresia.ACTIVA;
            case 2 -> estado == EstadoMembresia.VENCIDA;
            case 3 -> estado == EstadoMembresia.CONGELADA;
            case 4 -> estado == EstadoMembresia.INACTIVA;
            default -> true;
        };
    }

    private boolean dentro(LocalDate valor, LocalDate desde, LocalDate hasta) {
        if (valor == null) {
            return desde == null && hasta == null;
        }
        if (desde != null && valor.isBefore(desde)) {
            return false;
        }
        return hasta == null || !valor.isAfter(hasta);
    }

    private boolean dentro(LocalDateTime valor, LocalDate desde, LocalDate hasta) {
        return valor != null && dentro(valor.toLocalDate(), desde, hasta);
    }

    private boolean coincideTexto(String filtro, String... valores) {
        if (filtro == null || filtro.isBlank()) {
            return true;
        }
        String buscado = filtro.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(valores)
                .filter(Objects::nonNull)
                .map(valor -> valor.toLowerCase(Locale.ROOT))
                .anyMatch(valor -> valor.contains(buscado));
    }

    private String nombreCliente(Cliente cliente) {
        if (cliente == null) {
            return "";
        }
        return ((cliente.getNombre() == null ? "" : cliente.getNombre()) + " "
                + (cliente.getApellido() == null ? "" : cliente.getApellido())).trim();
    }

    private String planesTexto(Set<PlanesMembresia> planes) {
        if (planes == null || planes.isEmpty()) {
            return "";
        }
        return planes.stream()
                .map(PlanesMembresia::getNombre)
                .filter(Objects::nonNull)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.joining(", "));
    }

    private String entrenadoresTexto(Set<Usuario> entrenadores) {
        if (entrenadores == null || entrenadores.isEmpty()) {
            return "";
        }
        return entrenadores.stream()
                .map(Usuario::getUsuario)
                .filter(Objects::nonNull)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.joining(", "));
    }

    private String sexoTexto(Short sexo) {
        if (sexo == null) {
            return "";
        }
        return sexo == 0 ? "Masculino" : sexo == 1 ? "Femenino" : "";
    }

    private String metodoPagoTexto(Short metodo) {
        if (metodo == null) {
            return "Sin especificar";
        }
        return switch (metodo) {
            case 1 -> "Efectivo";
            case 2 -> "Tarjeta";
            case 3 -> "Transferencia";
            default -> "Otro";
        };
    }

    private String estadoPagoTexto(Short estado) {
        if (estado == null) {
            return "";
        }
        return switch (estado) {
            case 1 -> "Pendiente";
            case 2 -> "Pagado";
            case 3 -> "Anulado";
            default -> "";
        };
    }

    private BigDecimal promedio(List<BigDecimal> valores) {
        if (valores == null || valores.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal suma = valores.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long cantidad = valores.stream().filter(Objects::nonNull).count();
        return cantidad == 0 ? BigDecimal.ZERO
                : suma.divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal diferencia(BigDecimal fin, BigDecimal inicio) {
        if (fin == null || inicio == null) {
            return null;
        }
        return fin.subtract(inicio).setScale(2, RoundingMode.HALF_UP);
    }

    private Map<String, Object> fila(Object... datos) {
        Map<String, Object> fila = new LinkedHashMap<>();
        for (int i = 0; i < datos.length; i += 2) {
            fila.put((String) datos[i], datos[i + 1]);
        }
        return fila;
    }

    private List<ReporteColumna> columnasDe(ReporteTipo tipo) {
        return switch (tipo) {
            case MIEMBROS -> COLUMNAS_MIEMBROS;
            case MEMBRESIAS -> COLUMNAS_MEMBRESIAS;
            case INGRESOS -> COLUMNAS_INGRESOS;
            case MOROSIDAD -> COLUMNAS_MOROSIDAD;
            case ENTRENAMIENTOS -> COLUMNAS_ENTRENAMIENTOS;
            case EVALUACIONES -> COLUMNAS_EVALUACIONES;
            case COMPARATIVA -> COLUMNAS_COMPARATIVA;
            case FALLAS -> COLUMNAS_FALLAS;
            case AUDITORIA -> COLUMNAS_AUDITORIA;
        };
    }

    private boolean esPersonalizable(ReporteTipo tipo) {
        return tipo != ReporteTipo.COMPARATIVA && tipo != ReporteTipo.AUDITORIA;
    }
}
