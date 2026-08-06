package knou.cbt.web.admin;

import knou.cbt.domain.siteoperation.dto.SiteOperationSettingRequest;
import knou.cbt.domain.siteoperation.service.SiteOperationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 배포/점검 운영 설정 (점검 모드, 공지 배너) 관리자 화면
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/maintenance")
public class MaintenanceSettingController {

    private final SiteOperationSettingService settingService;

    @GetMapping
    public String form(Model model) {
        model.addAttribute("setting", settingService.get());
        return "admin/maintenance/maintenanceForm";
    }

    @PostMapping
    public String update(@ModelAttribute SiteOperationSettingRequest request) {
        settingService.update(request);
        return "redirect:/admin/maintenance";
    }
}
