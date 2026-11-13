package system_gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada de la aplicación Spring Boot.
 */
@SpringBootApplication
@EnableScheduling
public class SystemGymApplication {

	public static void main(String[] args) {
		SpringApplication.run(SystemGymApplication.class, args);
	}

}
