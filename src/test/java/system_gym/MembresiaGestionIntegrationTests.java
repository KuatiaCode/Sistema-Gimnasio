package system_gym;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Cliente;
import system_gym.model.MembresiaCliente;
import system_gym.model.MembresiaEvento;
import system_gym.model.PlanesMembresia;
import system_gym.model.TipoEventoMembresia;
import system_gym.repository.ClienteRepository;
import system_gym.repository.MembresiaClienteRepository;
import system_gym.repository.MembresiaEventoRepository;
import system_gym.repository.PlanesMembresiaRepository;
import system_gym.service.MembresiaClienteService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MembresiaGestionIntegrationTests {

    @Autowired
    private MembresiaClienteService membresiaClienteService;

    @Autowired
    private MembresiaClienteRepository membresiaClienteRepository;

    @Autowired
    private MembresiaEventoRepository membresiaEventoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PlanesMembresiaRepository planesMembresiaRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void actualizarEstadosMembresiasPersisteActivaVencidaYRespetaInactivaYCongelada() {
        LocalDate hoy = LocalDate.now();
        Cliente clienteActiva = crearCliente("estado-activa");
        Cliente clienteVencida = crearCliente("estado-vencida");
        Cliente clienteInactiva = crearCliente("estado-inactiva");
        Cliente clienteCongelada = crearCliente("estado-congelada");
        PlanesMembresia plan = crearPlan("estado");
        MembresiaCliente activa = crearMembresia(clienteActiva, plan, (short) 1,
                hoy.minusDays(5), hoy.plusDays(5));
        MembresiaCliente vencida = crearMembresia(clienteVencida, plan, (short) 1,
                hoy.minusDays(40), hoy.minusDays(1));
        MembresiaCliente inactiva = crearMembresia(clienteInactiva, plan, (short) 4,
                hoy.minusDays(60), hoy.minusDays(30));
        MembresiaCliente congelada = crearMembresia(clienteCongelada, plan, (short) 3,
                hoy.minusDays(10), hoy.plusDays(20));
        congelada.setFechaCongelacionInicio(hoy.minusDays(1));
        congelada.setFechaCongelacionFin(hoy.plusDays(5));
        membresiaClienteRepository.save(congelada);

        membresiaClienteService.actualizarEstadosMembresias();

        assertEquals((short) 1, recargar(activa).getEstado());
        assertEquals((short) 2, recargar(vencida).getEstado());
        assertEquals((short) 4, recargar(inactiva).getEstado());
        assertEquals((short) 3, recargar(congelada).getEstado());
    }

    @Test
    void crearNuevaActivaMarcaVencidaLaAnteriorDelMismoCliente() {
        LocalDate hoy = LocalDate.now();
        Cliente cliente = crearCliente("nueva-activa");
        PlanesMembresia plan = crearPlan("nueva-activa");
        MembresiaCliente anterior = crearMembresia(cliente, plan, (short) 1,
                hoy.minusDays(60), hoy.minusDays(1));

        MembresiaCliente nueva = new MembresiaCliente();
        nueva.setCliente(cliente);
        nueva.setPlanes(new LinkedHashSet<>(List.of(plan)));
        nueva.setFechaInicio(hoy);
        nueva.setFechaVencimiento(hoy.plusMonths(1));
        nueva.setEstado((short) 1);
        nueva.setRenovacionAutomatica(false);
        membresiaClienteService.guardar(nueva);

        assertEquals((short) 2, recargar(anterior).getEstado());
        assertEquals((short) 1, recargar(nueva).getEstado());
    }

    @Test
    void renovarActualizaVigenciaYRegistraEvento() {
        LocalDate hoy = LocalDate.now();
        Cliente cliente = crearCliente("renovar");
        PlanesMembresia plan = crearPlan("renovar");
        MembresiaCliente membresia = crearMembresia(cliente, plan, (short) 1,
                hoy.minusDays(20), hoy.plusDays(5));

        MembresiaEvento evento = membresiaClienteService.renovar(membresia.getId(),
                hoy.plusMonths(1), "Renovación mensual", "recepcion");

        MembresiaCliente actualizada = recargar(membresia);
        assertEquals((short) 1, actualizada.getEstado());
        assertEquals(hoy.plusMonths(1), actualizada.getFechaVencimiento());
        assertEquals(TipoEventoMembresia.RENOVACION, evento.getTipo());
        assertEquals("Renovación mensual", evento.getMotivo());
        assertEquals(hoy.plusDays(5), evento.getFechaVencimientoAnterior());
        assertEquals(hoy.plusMonths(1), evento.getFechaVencimientoNueva());
        assertEquals("recepcion", evento.getResponsable());
        assertEquals(1, membresiaEventoRepository
                .findByMembresiaIdOrderByFechaEventoDesc(membresia.getId()).size());
    }

    @Test
    void cancelarInactivaMembresiaYRegistraEvento() {
        LocalDate hoy = LocalDate.now();
        Cliente cliente = crearCliente("cancelar");
        PlanesMembresia plan = crearPlan("cancelar");
        MembresiaCliente membresia = crearMembresia(cliente, plan, (short) 1,
                hoy.minusDays(10), hoy.plusDays(20));

        MembresiaEvento evento = membresiaClienteService.cancelar(membresia.getId(), hoy,
                "Solicitud del cliente", "admin");

        MembresiaCliente actualizada = recargar(membresia);
        assertEquals((short) 4, actualizada.getEstado());
        assertEquals(hoy, actualizada.getFechaCancelacion());
        assertEquals("Solicitud del cliente", actualizada.getMotivoCancelacion());
        assertEquals(TipoEventoMembresia.CANCELACION, evento.getTipo());
        assertEquals("admin", evento.getResponsable());
    }

    @Test
    void congelarRegistraFechasYEvento() {
        LocalDate hoy = LocalDate.now();
        Cliente cliente = crearCliente("congelar");
        PlanesMembresia plan = crearPlan("congelar");
        MembresiaCliente membresia = crearMembresia(cliente, plan, (short) 1,
                hoy.minusDays(5), hoy.plusDays(20));

        MembresiaEvento evento = membresiaClienteService.congelar(membresia.getId(), hoy,
                hoy.plusDays(6), "Viaje", "recepcion");

        MembresiaCliente actualizada = recargar(membresia);
        assertEquals((short) 3, actualizada.getEstado());
        assertEquals(hoy, actualizada.getFechaCongelacionInicio());
        assertEquals(hoy.plusDays(6), actualizada.getFechaCongelacionFin());
        assertEquals("Viaje", actualizada.getMotivoCongelacion());
        assertEquals(TipoEventoMembresia.CONGELACION, evento.getTipo());
        assertEquals(7, evento.getDias());
    }

    @Test
    void finalizarCongelacionExtiendeVencimientoYReactiva() {
        LocalDate hoy = LocalDate.now();
        Cliente cliente = crearCliente("descongelar");
        PlanesMembresia plan = crearPlan("descongelar");
        MembresiaCliente membresia = crearMembresia(cliente, plan, (short) 3,
                hoy.minusDays(20), hoy.plusDays(10));
        membresia.setFechaCongelacionInicio(hoy.minusDays(7));
        membresia.setFechaCongelacionFin(hoy.minusDays(1));
        membresia.setMotivoCongelacion("Reposo");
        membresiaClienteRepository.save(membresia);

        MembresiaEvento evento = membresiaClienteService.descongelar(membresia.getId(), "admin");

        MembresiaCliente actualizada = recargar(membresia);
        assertEquals((short) 1, actualizada.getEstado());
        assertEquals(hoy.plusDays(17), actualizada.getFechaVencimiento());
        assertEquals(7, actualizada.getDiasCongelados());
        assertNull(actualizada.getFechaCongelacionInicio());
        assertNull(actualizada.getFechaCongelacionFin());
        assertEquals(TipoEventoMembresia.REACTIVACION, evento.getTipo());
        assertEquals(7, evento.getDias());
    }

    @Test
    void historialYComprobanteEstanDisponiblesParaRecepcion() throws Exception {
        LocalDate hoy = LocalDate.now();
        Cliente cliente = crearCliente("comprobante");
        PlanesMembresia plan = crearPlan("comprobante");
        MembresiaCliente membresia = crearMembresia(cliente, plan, (short) 1,
                hoy.minusDays(5), hoy.plusDays(10));
        MembresiaEvento evento = membresiaClienteService.renovar(membresia.getId(),
                hoy.plusMonths(1), "Renovación con comprobante", "recepcion");

        mockMvc.perform(get("/membresias/{id}/historial", membresia.getId())
                        .with(user("recepcion").roles("RECEPCIONISTA")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers
                        .containsString("Renovación con comprobante")));

        MvcResult resultado = mockMvc.perform(get("/membresias/{id}/comprobante/{eventoId}",
                        membresia.getId(), evento.getId())
                        .with(user("recepcion").roles("RECEPCIONISTA")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andReturn();

        byte[] pdf = resultado.getResponse().getContentAsByteArray();
        assertTrue(pdf.length > 100);
        assertEquals("%PDF", new String(pdf, 0, 4, StandardCharsets.US_ASCII));
    }

    @Test
    void recepcionPuedeRenovarDesdeElControlador() throws Exception {
        LocalDate hoy = LocalDate.now();
        Cliente cliente = crearCliente("controlador");
        PlanesMembresia plan = crearPlan("controlador");
        MembresiaCliente membresia = crearMembresia(cliente, plan, (short) 1,
                hoy.minusDays(5), hoy.plusDays(10));

        mockMvc.perform(post("/membresias/{id}/renovar", membresia.getId())
                        .with(user("recepcion").roles("RECEPCIONISTA"))
                        .with(csrf())
                        .param("fechaVencimiento", hoy.plusMonths(1).toString())
                        .param("motivo", "Renovación desde controlador"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/membresias/" + membresia.getId() + "/historial"))
                .andExpect(flash().attribute("mensaje", "Membresía renovada correctamente."));

        assertEquals(hoy.plusMonths(1), recargar(membresia).getFechaVencimiento());
        assertNotNull(membresiaEventoRepository
                .findByMembresiaIdOrderByFechaEventoDesc(membresia.getId()).getFirst());
    }

    private MembresiaCliente recargar(MembresiaCliente membresia) {
        return membresiaClienteRepository.findById(membresia.getId()).orElseThrow();
    }

    private Cliente crearCliente(String sufijo) {
        String marca = Long.toString(System.nanoTime() % 1_000_000_000L);
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente " + sufijo);
        cliente.setApellido(marca);
        cliente.setTelefono("0981000000");
        cliente.setDocumento("T" + marca);
        cliente.setCorreo("membresia_" + sufijo + "_" + marca + "@local");
        cliente.setFechaRegistro(LocalDateTime.now());
        cliente.setSexo((short) 0);
        cliente.setDireccion("Dirección de prueba");
        cliente.setCiudad("Asunción");
        cliente.setContactoEmergencia("Contacto");
        cliente.setTelefonoEmergencia("0981000001");
        cliente.setOcupacion("Prueba");
        return clienteRepository.save(cliente);
    }

    private PlanesMembresia crearPlan(String sufijo) {
        PlanesMembresia plan = new PlanesMembresia();
        plan.setNombre("Plan " + sufijo + " " + System.nanoTime());
        plan.setPrecio(150_000L);
        plan.setDescripcion("Plan de prueba");
        plan.setEstado((short) 1);
        return planesMembresiaRepository.save(plan);
    }

    private MembresiaCliente crearMembresia(Cliente cliente, PlanesMembresia plan, short estado,
            LocalDate inicio, LocalDate vencimiento) {
        MembresiaCliente membresia = new MembresiaCliente();
        membresia.setCliente(cliente);
        membresia.setPlanes(new LinkedHashSet<>(List.of(plan)));
        membresia.setFechaInicio(inicio);
        membresia.setFechaVencimiento(vencimiento);
        membresia.setEstado(estado);
        membresia.setPrecioAPagar(plan.getPrecio());
        membresia.setRenovacionAutomatica(false);
        membresia.setDiasCongelados(0);
        return membresiaClienteRepository.save(membresia);
    }
}
