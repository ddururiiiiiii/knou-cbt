package knou.cbt.web.exam;

import jakarta.servlet.http.HttpSession;
import knou.cbt.domain.exam.dto.AnswerForm;
import knou.cbt.domain.exam.dto.ExamResponse;
import knou.cbt.domain.exam.service.ExamService;
import knou.cbt.domain.examquestion.dto.ExamQuestionResponse;
import knou.cbt.domain.examquestion.service.ExamQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/exams/{examId}")
public class ExamSolveController {

    private final ExamService examService;
    private final ExamQuestionService examQuestionService;

    /**
     * 문제풀기 화면
     * @param examId
     * @param model
     * @return
     */
    @GetMapping("/solve")
    public String solve(@PathVariable("examId") Long examId, Model model) {
        ExamResponse exam = examService.get(examId);
        List<ExamQuestionResponse> questions = examQuestionService.getQuestions(examId);

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("answerForm", new AnswerForm());

        return "exam/solve";
    }

    /**
     * 제출 처리
     * @param examId
     * @param form
     * @param model
     * @return
     */
    @PostMapping("/solve")
    public String submit(@PathVariable("examId") Long examId,
                         @ModelAttribute AnswerForm form,
                         HttpSession session,
                         Model model) {
        ExamResponse exam = examService.get(examId);
        List<ExamQuestionResponse> questions = examQuestionService.getQuestions(examId);
        List<String> answers = form.getAnswers();

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            String userAnswer = answers.get(i);
            String correctAnswer = questions.get(i).answers();

            if (userAnswer != null && userAnswer.equals(correctAnswer)) {
                score++;
            }
        }
        session.setAttribute("userAnswers", answers);
        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("score", score);

        return "exam/result";
    }


    /**
     * 답안 확인하기
     * @param examId
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/review")
    public String review(@PathVariable("examId") Long examId,
                         HttpSession session,
                         Model model) {

        ExamResponse exam = examService.get(examId);
        List<ExamQuestionResponse> questions = examQuestionService.getQuestions(examId);

        // 세션에서 꺼내기
        List<String> answers = (List<String>) session.getAttribute("userAnswers");

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (answers != null && answers.get(i).equals(questions.get(i).answers())) {
                score++;
            }
        }
        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("userAnswers", answers);
        model.addAttribute("score", score);

        return "exam/review";
    }

}
