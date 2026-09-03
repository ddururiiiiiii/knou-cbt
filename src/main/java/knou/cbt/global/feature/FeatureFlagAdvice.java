package knou.cbt.global.feature;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 화면을 그리는 모든 컨트롤러에 feature flag를 모델로 내려준다.
 * 네비게이션의 로그인/회원가입 버튼 노출 여부(app.member-login.enabled)에 사용.
 */
@ControllerAdvice(annotations = Controller.class)
public class FeatureFlagAdvice {

    @Value("${app.member-login.enabled:false}")
    private boolean memberLoginEnabled;

    @ModelAttribute("memberLoginEnabled")
    public boolean memberLoginEnabled() {
        return memberLoginEnabled;
    }
}
