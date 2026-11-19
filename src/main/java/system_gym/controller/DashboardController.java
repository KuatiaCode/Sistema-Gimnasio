package system_gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import system_gym.service.DashboardService;

/**
 * Página principal del sistema; atiende tanto la raíz como /dashboard.
 */
@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard"})
    public String index(Model model) {
        model.addAttribute("title", "Dashboard");
        model.addAttribute("resumen", dashboardService.resumen());
        return "dashboard/index";
    }
}
