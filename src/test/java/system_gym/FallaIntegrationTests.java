package system_gym;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Falla;
import system_gym.model.Roles;
import system_gym.model.Usuario;
import system_gym.repository.FallaRepository;
import system_gym.repository.RolesRepository;
import system_gym.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FallaIntegrationTests {

    private static final String USUARIO_RECEPCION = "recepcion_falla_test";
    private static final String USUARIO_ADMIN = "admin_falla_test";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FallaRepository fallaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario recepcion;
    private Usuario admin;

    @BeforeEach
    void crearUsuariosDePrueba() {
        recepcion = crearUsuario(USUARIO_RECEPCION, "RECEPCIONISTA");
        admin = crearUsuario(USUARIO_ADMIN, "ADMIN");
    }

    @Test
    void formularioNuevoMuestraReportadoPorSoloLectura() throws Exception {
        mockMvc.perform(get("/fallas/nuevo")
                        .with(user(USUARIO_RECEPCION).roles("RECEPCIONISTA")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"reportadoPor\"")))
                .andExpect(content().string(containsString("readonly")))
                .andExpect(content().string(not(containsString("name=\"reportadoPor\""))));
    }

    @Test
    void crearFallaInvalidaMuestraErroresYNoGuarda() throws Exception {
        long antes = fallaRepository.count();

        mockMvc.perform(post("/fallas")
                        .with(user(USUARIO_RECEPCION).roles("RECEPCIONISTA"))
                        .with(csrf())
                        .param("reportadoPor", "otro")
                        .param("areaEquipo", "")
                        .param("descripcion", "corta")
                        .param("prioridad", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("fallas/form"))
                .andExpect(model().attributeHasFieldErrors("falla", "areaEquipo", "descripcion"));

        assertEquals(antes, fallaRepository.count());
    }

    @Test
    void crearFallaIgnoraReportadoPorEnviado() throws Exception {
        mockMvc.perform(post("/fallas")
                        .with(user(USUARIO_RECEPCION).roles("RECEPCIONISTA"))
                        .with(csrf())
                        .param("reportadoPor", "otro")
                        .param("areaEquipo", "Sala de pesas")
                        .param("descripcion", "Descripción creada desde el controlador")
                        .param("prioridad", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fallas"))
                .andExpect(flash().attribute("mensaje", "Falla registrada correctamente."));

        Falla creada = fallaRepository.findAll().stream()
                .filter(falla -> "Descripción creada desde el controlador".equals(falla.getDescripcion()))
                .findFirst()
                .orElseThrow();

        assertEquals(USUARIO_RECEPCION, creada.getReportadoPor());
        assertEquals(USUARIO_RECEPCION, creada.getUsuarioReporta().getUsuario());
        assertEquals((short) 1, creada.getEstado());
    }

    @Test
    void recepcionistaNoPuedeEditarFalla() throws Exception {
        Falla falla = crearFalla(recepcion, "Descripción original válida");

        mockMvc.perform(post("/fallas")
                        .with(user(USUARIO_RECEPCION).roles("RECEPCIONISTA"))
                        .with(csrf())
                        .param("id", falla.getId().toString())
                        .param("areaEquipo", "Área modificada")
                        .param("descripcion", "Descripción modificada válida")
                        .param("prioridad", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fallas"))
                .andExpect(flash().attribute("error", "Solo el administrador puede modificar una falla."));

        Falla actual = fallaRepository.findById(falla.getId()).orElseThrow();
        assertEquals("Descripción original válida", actual.getDescripcion());
        assertEquals((short) 1, actual.getEstado());
    }

    @Test
    void adminPuedeEditarFallaYConservaReportadoPor() throws Exception {
        Falla falla = crearFalla(recepcion, "Descripción original válida");

        mockMvc.perform(post("/fallas")
                        .with(user(USUARIO_ADMIN).roles("ADMIN"))
                        .with(csrf())
                        .param("id", falla.getId().toString())
                        .param("areaEquipo", "Área actualizada")
                        .param("descripcion", "Descripción actualizada válida")
                        .param("prioridad", "3")
                        .param("estado", "3")
                        .param("responsableId", admin.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fallas"))
                .andExpect(flash().attribute("mensaje", "Falla actualizada correctamente."));

        Falla actual = fallaRepository.findById(falla.getId()).orElseThrow();
        assertEquals("Área actualizada", actual.getAreaEquipo());
        assertEquals((short) 3, actual.getEstado());
        assertEquals(USUARIO_RECEPCION, actual.getReportadoPor());
        assertEquals(USUARIO_ADMIN, actual.getResponsableAtencion().getUsuario());
        assertNotNull(actual.getFechaCierre());
    }

    private Falla crearFalla(Usuario usuario, String descripcion) {
        Falla falla = new Falla();
        falla.setUsuarioReporta(usuario);
        falla.setReportadoPor(usuario.getUsuario());
        falla.setAreaEquipo("Sala de pesas");
        falla.setDescripcion(descripcion);
        falla.setPrioridad((short) 2);
        falla.setEstado((short) 1);
        falla.setFechaReporte(LocalDateTime.now());
        return fallaRepository.save(falla);
    }

    private Usuario crearUsuario(String nombre, String codigoRol) {
        return usuarioRepository.findByUsuario(nombre).orElseGet(() -> {
            Roles rol = rolesRepository.findByCodigo(codigoRol)
                    .orElseThrow(() -> new IllegalStateException("Falta el rol " + codigoRol));

            Usuario usuario = new Usuario();
            usuario.setUsuario(nombre);
            usuario.setCorreo(nombre + "@local");
            usuario.setPasswordHash(passwordEncoder.encode("clave-prueba"));
            usuario.setRoles(rol);
            usuario.setIntentosFallidos(0);
            return usuarioRepository.save(usuario);
        });
    }
}
