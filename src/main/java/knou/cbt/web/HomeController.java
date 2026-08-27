package knou.cbt.web;

import knou.cbt.domain.department.service.DepartmentService;
import knou.cbt.domain.exam.service.ExamService;
import knou.cbt.domain.notice.service.NoticeService;
import knou.cbt.domain.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final NoticeService noticeService;
    private final DepartmentService departmentService;
    private final SubjectService subjectService;
    private final ExamService examService;

    /**
     * 메인화면 (학생용)
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("recentNotices", noticeService.getHomeNotices());
        model.addAttribute("departmentCount", departmentService.count(null, "Y"));
        model.addAttribute("subjectCount", subjectService.count(null, "Y"));
        model.addAttribute("examCount", examService.count("Y", null, null, null, null));
        model.addAttribute("popularSubjects", subjectService.findTopSubjectsByExamCount(4));
        model.addAttribute("departments", departmentService.findAll());
        return "index";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }
}
