package knou.cbt.web.admin;

import knou.cbt.common.api.PageRequest;
import knou.cbt.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class MemberAdminController {

    private final MemberService memberService;

    @GetMapping
    public String list(PageRequest pageRequest, @RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("pagination", memberService.listPage(keyword, pageRequest));
        model.addAttribute("keyword", keyword);
        return "admin/member/memberList";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        memberService.withdraw(id);
        return "redirect:/admin/members";
    }
}
