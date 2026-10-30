package system_gym.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import system_gym.model.RegistroAuditoria;
import system_gym.service.AuditoriaService;

@Controller
@RequestMapping("/configuracion/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                20,
                Sort.by(Sort.Order.desc("fechaHora"), Sort.Order.desc("id")));
        Page<RegistroAuditoria> registros = auditoriaService.listar(pageable);
        model.addAttribute("registros", registros);
        return "configuracion/auditoria";
    }
}
