package knou.cbt.global.maintenance;

import knou.cbt.domain.siteoperation.model.SiteOperationSetting;
import knou.cbt.domain.siteoperation.service.SiteOperationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 화면을 그리는 모든 컨트롤러에 사전 공지 배너 정보를 모델로 내려준다. (layout.html 상단에서 노출)
 */
@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class OperationBannerAdvice {

    private final SiteOperationSettingService settingService;

    @ModelAttribute("operationBanner")
    public SiteOperationSetting operationBanner() {
        SiteOperationSetting setting = settingService.get();
        return Boolean.TRUE.equals(setting.getBannerEnabled()) ? setting : null;
    }
}
