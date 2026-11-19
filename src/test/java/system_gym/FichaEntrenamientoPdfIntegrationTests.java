package system_gym;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import system_gym.model.FichaEntrenamiento;
import system_gym.model.MembresiaCliente;
import system_gym.model.PlanesMembresia;
import system_gym.model.Roles;
import system_gym.model.Usuario;
import system_gym.repository.ClienteRepository;
import system_gym.repository.FichaEntrenamientoRepository;
import system_gym.repository.MembresiaClienteRepository;
import system_gym.repository.PlanesMembresiaRepository;
import system_gym.repository.RolesRepository;
import system_gym.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FichaEntrenamientoPdfIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PlanesMembresiaRepository planesMembresiaRepository;

    @Autowired
    private MembresiaClienteRepository membresiaClienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private FichaEntrenamientoRepository fichaEntrenamientoRepository;

    @Test
    void entrenadorPuedeDescargarLaFichaIndividualEnPdf() throws Exception {
        FichaEntrenamiento ficha = crearFicha();

        mockMvc.perform(get("/fichas-entrenamiento")
                        .with(user("entrenador").roles("ENTRENADOR")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "/fichas-entrenamiento/" + ficha.getId() + "/pdf")));

        MvcResult resultado = mockMvc.perform(get("/fichas-entrenamiento/{id}/pdf", ficha.getId())
                        .with(user("entrenador").roles("ENTRENADOR")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition",
                        containsString("ficha-entrenamiento-" + ficha.getId() + ".pdf")))
                .andReturn();

        byte[] pdf = resultado.getResponse().getContentAsByteArray();
        assertTrue(pdf.length > 100);
        assertEquals("%PDF", new String(pdf, 0, 4, StandardCharsets.US_ASCII));
    }

    @Test
    void recepcionistaNoPuedeDescargarLaFichaEnPdf() throws Exception {
        FichaEntrenamiento ficha = crearFicha();

        mockMvc.perform(get("/fichas-entrenamiento/{id}/pdf", ficha.getId())
                        .with(user("recepcion").roles("RECEPCIONISTA")))
                .andExpect(status().isForbidden());
    }

    private FichaEntrenamiento crearFicha() {
        String marca = Long.toString(System.nanoTime() % 1_000_000_000L);
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente");
        cliente.setApellido("Ficha " + marca);
        cliente.setTelefono("0981000000");
        cliente.setDocumento("F" + marca);
        cliente.setCorreo("ficha_" + marca + "@local");
        cliente.setFechaRegistro(LocalDateTime.now());
        cliente.setSexo((short) 0);
        cliente.setDireccion("Dirección de prueba");
        cliente.setCiudad("Asunción");
        cliente.setContactoEmergencia("Contacto");
        cliente.setTelefonoEmergencia("0981000001");
        cliente.setOcupacion("Prueba");
        cliente = clienteRepository.save(cliente);

        PlanesMembresia plan = new PlanesMembresia();
        plan.setNombre("Plan ficha " + marca);
        plan.setPrecio(150_000L);
        plan.setDescripcion("Fuerza, movilidad y acondicionamiento");
        plan.setEstado((short) 1);
        plan = planesMembresiaRepository.save(plan);

        MembresiaCliente membresia = new MembresiaCliente();
        membresia.setCliente(cliente);
        membresia.setPlanes(new LinkedHashSet<>(List.of(plan)));
        membresia.setFechaInicio(LocalDate.now().minusDays(5));
        membresia.setFechaVencimiento(LocalDate.now().plusDays(25));
        membresia.setEstado((short) 1);
        membresia.setPrecioAPagar(plan.getPrecio());
        membresia.setRenovacionAutomatica(false);
        membresia.setDiasCongelados(0);
        membresia = membresiaClienteRepository.save(membresia);

        Roles rol = rolesRepository.findByCodigo("ENTRENADOR")
                .orElseThrow(() -> new IllegalStateException("Falta el rol ENTRENADOR"));
        Usuario entrenador = new Usuario();
        entrenador.setUsuario("entrenador_ficha_" + marca);
        entrenador.setCorreo("entrenador_ficha_" + marca + "@local");
        entrenador.setPasswordHash("clave-prueba");
        entrenador.setRoles(rol);
        entrenador.setIntentosFallidos(0);
        entrenador = usuarioRepository.save(entrenador);

        FichaEntrenamiento ficha = new FichaEntrenamiento();
        ficha.setCliente(cliente);
        ficha.setMembresia(membresia);
        ficha.setEntrenadores(new LinkedHashSet<>(List.of(entrenador)));
        ficha.setFechaCreacion(LocalDateTime.now());
        ficha.setObjetivo("Hipertrofia y fuerza");
        ficha.setDiasEntrenamiento(new LinkedHashSet<>(List.of((short) 1, (short) 3, (short) 5)));
        return fichaEntrenamientoRepository.save(ficha);
    }
}
