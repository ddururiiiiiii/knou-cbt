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

import java.util.ArrayList;
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
            // answers가 비어있거나 인덱스 범위를 벗어나면 null 처리
            String userAnswer = (answers != null && i < answers.size()) ? answers.get(i) : null;
            String correctAnswer = questions.get(i).answers();

            if (userAnswer != null && userAnswer.equals(correctAnswer)) {
                score++;
            }
        }

        // 문제 개수와 동일한 길이로 보정 (리뷰 화면에서 IndexError 방지)
        while (answers.size() < questions.size()) {
            answers.add(null);
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

        List<String> answers = (List<String>) session.getAttribute("userAnswers");
        if (answers == null) {
            answers = new ArrayList<>();
        }

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            String userAnswer = (i < answers.size()) ? answers.get(i) : null;
            String correctAnswer = questions.get(i).answers();

            if (userAnswer != null && userAnswer.equals(correctAnswer)) {
                score++;
            }
        }

        // 리뷰에서도 길이 보정
        while (answers.size() < questions.size()) {
            answers.add(null);
        }

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("userAnswers", answers);
        model.addAttribute("score", score);

        return "exam/review";
    }

}
