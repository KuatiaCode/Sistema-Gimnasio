package system_gym.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import system_gym.model.RegistroAuditoria;
import system_gym.service.AuditoriaService;

@Component
public class AuditoriaInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaInterceptor.class);
    private static final String INICIO = AuditoriaInterceptor.class.getName() + ".inicio";

    private final AuditoriaService auditoriaService;

    public AuditoriaInterceptor(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(INICIO, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            Object inicio = request.getAttribute(INICIO);
            long duracion = inicio instanceof Long valor ? System.currentTimeMillis() - valor : 0L;
            String ruta = obtenerRuta(request);
            int codigoHttp = obtenerCodigoHttp(request, response, ex);

            RegistroAuditoria registro = new RegistroAuditoria();
            registro.setUsuario(obtenerUsuario(request));
            registro.setRol(obtenerRol(request));
            registro.setAccion(obtenerAccion(request.getMethod(), ruta));
            registro.setModulo(obtenerModulo(ruta));
            registro.setMetodoHttp(request.getMethod());
            registro.setRuta(ruta);
            registro.setIp(request.getRemoteAddr());
            registro.setCodigoHttp(codigoHttp);
            registro.setResultado(codigoHttp < 400 ? "EXITO" : "ERROR");
            registro.setDuracionMs(duracion);
            registro.setDescripcion(request.getMethod() + " " + ruta);
            auditoriaService.registrar(registro);
        } catch (Exception e) {
            log.warn("No se pudo registrar la auditoría de la petición {}", obtenerRuta(request), e);
        }
    }

    private String obtenerRuta(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int separador = uri.indexOf(';');
        return separador >= 0 ? uri.substring(0, separador) : uri;
    }

    private int obtenerCodigoHttp(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        if (ex != null) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        Object estadoError = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (estadoError instanceof Integer estado) {
            return estado;
        }
        return response.getStatus();
    }

    private String obtenerUsuario(HttpServletRequest request) {
        return request.getUserPrincipal() == null ? "anónimo" : request.getUserPrincipal().getName();
    }

    private String obtenerRol(HttpServletRequest request) {
        return request.isUserInRole("ROLE_ADMIN") ? "ADMIN" : null;
    }

    private String obtenerAccion(String metodoHttp, String ruta) {
        if ("GET".equals(metodoHttp)) {
            return "CONSULTA";
        }
        if (ruta.endsWith("/eliminar")) {
            return "ELIMINACION";
        }
        return "GUARDAR";
    }

    private String obtenerModulo(String uri) {
        if (uri.startsWith("/configuracion/roles")) {
            return "Roles";
        }
        if (uri.startsWith("/configuracion/usuarios")) {
            return "Usuarios";
        }
        if (uri.startsWith("/configuracion/auditoria")) {
            return "Auditoría";
        }
        if (uri.startsWith("/configuracion")) {
            return "Configuración";
        }
        if (uri.startsWith("/clientes")) {
            return "Clientes";
        }
        if (uri.startsWith("/membresias")) {
            return "Membresías";
        }
        if (uri.startsWith("/planes")) {
            return "Planes";
        }
        if (uri.startsWith("/pagos")) {
            return "Pagos";
        }
        if (uri.startsWith("/fichas-entrenamiento")) {
            return "Fichas de entrenamiento";
        }
        if (uri.startsWith("/fichas-medica")) {
            return "Fichas médicas";
        }
        if (uri.equals("/") || uri.startsWith("/dashboard")) {
            return "Dashboard";
        }
        return "Sistema";
    }
}
