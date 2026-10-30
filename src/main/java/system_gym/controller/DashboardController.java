package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página principal del sistema; atiende tanto la raíz como /dashboard.
 */
@Controller 
public class DashboardController {

    @GetMapping({"/", "/dashboard"})
    public String index(Model model) {
        model.addAttribute("title", "Dashboard - Oficina del Cuerpo");
        return "dashboard/index";
    }
}
