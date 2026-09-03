package knou.cbt.web.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knou.cbt.common.api.PageRequest;
import knou.cbt.domain.department.service.DepartmentService;
import knou.cbt.domain.exam.dto.ExamResponse;
import knou.cbt.domain.exam.service.ExamService;
import knou.cbt.domain.examquestion.dto.ExamQuestionResponse;
import knou.cbt.domain.examquestion.service.ExamQuestionService;
import knou.cbt.domain.member.service.MemberService;
import knou.cbt.domain.statistics.dto.AttemptHistoryResponse;
import knou.cbt.domain.statistics.service.StatisticsService;
import knou.cbt.global.security.MemberSecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final StatisticsService statisticsService;
    private final DepartmentService departmentService;
    private final ExamService examService;
    private final ExamQuestionService examQuestionService;

    @GetMapping
    public String myPage(PageRequest pageRequest, Model model) {
        Long memberId = MemberSecurityUtils.currentMemberId();

        model.addAttribute("member", memberService.get(memberId));
        model.addAttribute("pagination", statisticsService.getMemberAttemptHistory(memberId, pageRequest));
        model.addAttribute("departments", departmentService.findAll());

        return "member/mypage";
    }

    @GetMapping("/attempts/{attemptId}")
    public String attemptDetail(@PathVariable Long attemptId, Model model) {
        Long memberId = MemberSecurityUtils.currentMemberId();
        AttemptHistoryResponse attempt = statisticsService.getMemberAttemptDetail(attemptId, memberId);

        ExamResponse exam = examService.get(attempt.examId());
        List<ExamQuestionResponse> questions = examQuestionService.getQuestions(attempt.examId());

        String answersCsv = attempt.answers() != null ? attempt.answers() : "";
        List<String> userAnswers = new ArrayList<>(List.of(answersCsv.split(",", -1)));
        while (userAnswers.size() < questions.size()) {
            userAnswers.add("");
        }

        List<List<String>> correctAnswersList = new ArrayList<>();
        for (ExamQuestionResponse q : questions) {
            correctAnswersList.add(List.of(q.answers().split(",")).stream().map(String::trim).toList());
        }

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("userAnswers", userAnswers);
        model.addAttribute("correctAnswersList", correctAnswersList);
        model.addAttribute("score", attempt.score());
        model.addAttribute("elapsedSeconds", attempt.elapsedSeconds() != null ? attempt.elapsedSeconds() : 0);
        model.addAttribute("backUrl", "/mypage");
        model.addAttribute("backLabel", "마이페이지로");

        return "exam/review";
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
