package system_gym;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Roles;
import system_gym.model.Usuario;
import system_gym.repository.RolesRepository;
import system_gym.repository.UsuarioRepository;
import system_gym.service.ConfiguracionService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTests {

    private static final String USUARIO_PRUEBA = "usuario_prueba";
    private static final String PASSWORD_PRUEBA = "clave-segura";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConfiguracionService configuracionService;

    @BeforeEach
    void crearUsuarioDePrueba() {
        if (usuarioRepository.findByUsuario(USUARIO_PRUEBA).isPresent()) {
            return;
        }
        Roles rol = rolesRepository.findByCodigo("RECEPCIONISTA")
                .orElseThrow(() -> new IllegalStateException("Falta el rol RECEPCIONISTA"));

        Usuario usuario = new Usuario();
        usuario.setUsuario(USUARIO_PRUEBA);
        usuario.setCorreo("usuario_prueba@local");
        usuario.setPasswordHash(passwordEncoder.encode(PASSWORD_PRUEBA));
        usuario.setRoles(rol);
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
    }

    @Test
    void paginaDeLoginEsPublica() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void rutaProtegidaSinSesionRedirigeAlLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void loginConCredencialesValidasRedirigeAlDashboard() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", USUARIO_PRUEBA)
                        .param("password", PASSWORD_PRUEBA)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void loginConCredencialesInvalidasRedirigeAlLoginConError() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", USUARIO_PRUEBA)
                        .param("password", "incorrecta")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void loginAplicaElTiempoDeSesionConfigurado() throws Exception {
        Short tiempo = configuracionService.obtenerConfiguracion().getTiempoSesion();
        int minutos = tiempo == null || tiempo <= 0 ? 30 : tiempo;
        MockHttpSession sesion = new MockHttpSession();

        mockMvc.perform(post("/login")
                        .session(sesion)
                        .param("username", USUARIO_PRUEBA)
                        .param("password", PASSWORD_PRUEBA)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertEquals(minutos * 60, sesion.getMaxInactiveInterval());
    }

    @Test
    void administradorPuedeAccederAConfiguracion() throws Exception {
        mockMvc.perform(get("/configuracion").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void recepcionistaNoPuedeAccederAConfiguracion() throws Exception {
        mockMvc.perform(get("/configuracion").with(user("recepcion").roles("RECEPCIONISTA")))
                .andExpect(status().isForbidden());
    }

    @Test
    void recepcionistaPuedeAccederAPagos() throws Exception {
        mockMvc.perform(get("/pagos").with(user("recepcion").roles("RECEPCIONISTA")))
                .andExpect(status().isOk());
    }

    @Test
    void entrenadorPuedeAccederAFichasDeEntrenamiento() throws Exception {
        mockMvc.perform(get("/fichas-entrenamiento").with(user("entrenador").roles("ENTRENADOR")))
                .andExpect(status().isOk());
    }

    @Test
    void entrenadorNoPuedeAccederAPagos() throws Exception {
        mockMvc.perform(get("/pagos").with(user("entrenador").roles("ENTRENADOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void recepcionistaPuedeAccederAReportes() throws Exception {
        mockMvc.perform(get("/reportes").with(user("recepcion").roles("RECEPCIONISTA")))
                .andExpect(status().isOk());
    }

    @Test
    void recepcionistaNoPuedeAccederAAuditoria() throws Exception {
        mockMvc.perform(get("/reportes/auditoria").with(user("recepcion").roles("RECEPCIONISTA")))
                .andExpect(status().isForbidden());
    }

    @Test
    void entrenadorPuedeAccederAReporteDeEntrenamientos() throws Exception {
        mockMvc.perform(get("/reportes/entrenamientos").with(user("entrenador").roles("ENTRENADOR")))
                .andExpect(status().isOk());
    }

    @Test
    void entrenadorPuedeAccederAFallasPeroNoEditarlas() throws Exception {
        mockMvc.perform(get("/fallas").with(user("entrenador").roles("ENTRENADOR")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/fallas/1/editar").with(user("entrenador").roles("ENTRENADOR")))
                .andExpect(status().isForbidden());
    }
}
