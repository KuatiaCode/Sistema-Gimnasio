package system_gym.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Roles;
import system_gym.model.Usuario;
import system_gym.repository.RolesRepository;
import system_gym.repository.UsuarioRepository;

/**
 * Crea los roles base y el administrador inicial cuando la base está vacía.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);

    private final RolesRepository rolesRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsuario;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.correo:admin@local}")
    private String adminCorreo;

    public DatosIniciales(RolesRepository rolesRepository, UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.rolesRepository = rolesRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Roles admin = crearRolSiNoExiste("ADMIN", "Administrador",
                "Acceso completo a la configuración y a todos los módulos.");
        crearRolSiNoExiste("RECEPCIONISTA", "Recepcionista",
                "Gestión de clientes, planes, membresías y pagos.");
        crearRolSiNoExiste("ENTRENADOR", "Entrenador",
                "Gestión de clientes y fichas de entrenamiento y médicas.");

        if (usuarioRepository.findByUsuario(adminUsuario).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsuario(adminUsuario);
            usuario.setCorreo(adminCorreo);
            usuario.setPasswordHash(passwordEncoder.encode(adminPassword));
            usuario.setRoles(admin);
            usuario.setDosPasosActivo(false);
            usuario.setIntentosFallidos(0);
            usuarioRepository.save(usuario);
            log.info("Usuario administrador inicial creado: {}", adminUsuario);
        }
    }

    private Roles crearRolSiNoExiste(String codigo, String nombre, String descripcion) {
        return rolesRepository.findByCodigo(codigo).orElseGet(() -> {
            Roles rol = new Roles();
            rol.setCodigo(codigo);
            rol.setNombre(nombre);
            rol.setDescripcion(descripcion);
            return rolesRepository.save(rol);
        });
    }
}
