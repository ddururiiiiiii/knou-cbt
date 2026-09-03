package knou.cbt.web.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knou.cbt.common.api.PageRequest;
import knou.cbt.domain.department.service.DepartmentService;
import knou.cbt.domain.member.service.MemberService;
import knou.cbt.domain.statistics.service.StatisticsService;
import knou.cbt.global.security.MemberSecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final StatisticsService statisticsService;
    private final DepartmentService departmentService;

    @GetMapping
    public String myPage(PageRequest pageRequest, Model model) {
        Long memberId = MemberSecurityUtils.currentMemberId();

        model.addAttribute("member", memberService.get(memberId));
        model.addAttribute("pagination", statisticsService.getMemberAttemptHistory(memberId, pageRequest));
        model.addAttribute("departments", departmentService.findAll());

        return "member/mypage";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String nickname,
                                 @RequestParam(required = false) Long departmentId) {
        memberService.updateProfile(MemberSecurityUtils.currentMemberId(), nickname, departmentId);
        return "redirect:/mypage";
    }

    @PostMapping("/withdraw")
    public String withdraw(HttpServletRequest request, HttpServletResponse response) {
        memberService.withdraw(MemberSecurityUtils.currentMemberId());
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/";
    }
}
