package knou.cbt.web;

import knou.cbt.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final NoticeService noticeService;

    /**
     * 메인화면 (학생용)
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("recentNotices", noticeService.getHomeNotices());
        return "index";
    }
}
