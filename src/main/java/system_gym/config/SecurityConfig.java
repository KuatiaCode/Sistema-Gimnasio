package system_gym.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import system_gym.security.AutenticacionExitosaHandler;

/**
 * Define el acceso por rol a los módulos y la política de sesiones.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AutenticacionExitosaHandler autenticacionExitosaHandler;

    public SecurityConfig(AutenticacionExitosaHandler autenticacionExitosaHandler) {
        this.autenticacionExitosaHandler = autenticacionExitosaHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(autorizacion -> autorizacion
                        .requestMatchers("/login", "/css/**", "/js/**", "/error", "/favicon.ico",
                                "/actuator/health")
                        .permitAll()
                        .requestMatchers("/configuracion/**", "/roles/**", "/usuarios/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/membresias/**", "/planes/**", "/pagos/**")
                        .hasAnyRole("ADMIN", "RECEPCIONISTA")
                        .requestMatchers("/fichas-entrenamiento/**", "/fichas-medica/**")
                        .hasAnyRole("ADMIN", "ENTRENADOR")
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(autenticacionExitosaHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .sessionManagement(sesion -> sesion.invalidSessionUrl("/login?expired"))
                .csrf(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
