package knou.cbt.web.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/debug")
public class AdminDebugController {

    // Sentry 알림 연동이 실제로 동작하는지 확인하기 위한 강제 예외 발생 엔드포인트.
    // /admin/** 이라 SecurityConfig에 의해 ROLE_ADMIN만 접근 가능.
    @GetMapping("/test-error")
    public String testError() {
        throw new RuntimeException("Sentry 알림 테스트용 강제 예외");
    }
}
