package system_gym.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registra los interceptores MVC de la aplicación.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuditoriaInterceptor auditoriaInterceptor;

    public WebConfig(AuditoriaInterceptor auditoriaInterceptor) {
        this.auditoriaInterceptor = auditoriaInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Los recursos estáticos y la página de error no se auditan.
        registry.addInterceptor(auditoriaInterceptor)
                .excludePathPatterns("/css/**", "/js/**", "/error", "/favicon.ico");
    }
}
