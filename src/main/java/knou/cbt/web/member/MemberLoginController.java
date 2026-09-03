package knou.cbt.web.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberLoginController {

    /**
     * 회원(소셜) 로그인 페이지. feature flag로 네비게이션에는 안 보일 수 있지만
     * 경로 자체는 항상 열려있어 직접 테스트 가능하다.
     */
    @GetMapping("/login")
    public String loginPage() {
        return "member/login";
    }
}
