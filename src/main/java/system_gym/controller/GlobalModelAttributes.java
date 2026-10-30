package system_gym.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import system_gym.model.Configuracion;
import system_gym.service.ConfiguracionService;

/**
 * Expone el nombre y las iniciales del gimnasio a todas las vistas.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private static final String NOMBRE_POR_DEFECTO = "Oficina del Cuerpo";
    private static final Set<String> CONECTORES = Set.of("de", "del", "la", "las", "el", "los", "y", "e");

    private final ConfiguracionService configuracionService;

    public GlobalModelAttributes(ConfiguracionService configuracionService) {
        this.configuracionService = configuracionService;
    }

    @ModelAttribute
    public void agregarDatosDelGimnasio(Model model) {
        Configuracion configuracion = configuracionService.obtenerConfiguracion();
        String nombre = normalizarNombre(configuracion.getNombre());
        model.addAttribute("nombreGimnasio", nombre);
        model.addAttribute("inicialesGimnasio", obtenerIniciales(nombre));
    }

    private String normalizarNombre(String nombre) {
        return nombre == null || nombre.isBlank() ? NOMBRE_POR_DEFECTO : nombre.trim();
    }

    // Ignora conectores para que "Oficina del Cuerpo" genere "OC".
    private String obtenerIniciales(String nombre) {
        List<String> palabras = Arrays.stream(nombre.split("\\s+"))
                .filter(palabra -> !palabra.isBlank())
                .filter(palabra -> !CONECTORES.contains(palabra.toLowerCase(Locale.ROOT)))
                .toList();

        if (palabras.isEmpty()) {
            return "OC";
        }
        if (palabras.size() == 1) {
            String palabra = palabras.get(0);
            return palabra.substring(0, Math.min(2, palabra.length())).toUpperCase(Locale.ROOT);
        }
        return (palabras.get(0).substring(0, 1) + palabras.get(1).substring(0, 1)).toUpperCase(Locale.ROOT);
    }
}
