package system_gym.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import system_gym.service.ConfiguracionService;

/**
 * Aplica el tiempo de inactividad configurado al iniciar una sesión autenticada.
 */
@Component
public class AutenticacionExitosaHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final ConfiguracionService configuracionService;

    public AutenticacionExitosaHandler(ConfiguracionService configuracionService) {
        this.configuracionService = configuracionService;
        setDefaultTargetUrl("/dashboard");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        Short tiempoSesion = configuracionService.obtenerConfiguracion().getTiempoSesion();
        int minutos = tiempoSesion == null || tiempoSesion <= 0 ? 30 : tiempoSesion;

        request.getSession(true).setMaxInactiveInterval(minutos * 60);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
