package knou.cbt.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * 관리자 로그인 페이지
     * @return
     */
    @GetMapping("/admin/login")
    public String loginPage() {
        return "admin/login";
    }
}
