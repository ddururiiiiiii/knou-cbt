package knou.cbt.web.exam;

import jakarta.servlet.http.HttpSession;
import knou.cbt.domain.exam.dto.AnswerForm;
import knou.cbt.domain.exam.dto.ExamResponse;
import knou.cbt.domain.exam.exception.ExamNotFoundException;
import knou.cbt.domain.exam.service.ExamService;
import knou.cbt.domain.examquestion.dto.ExamQuestionResponse;
import knou.cbt.domain.examquestion.service.ExamQuestionService;
import knou.cbt.domain.statistics.service.StatisticsService;
import knou.cbt.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/exams/{examId}")
public class ExamSolveController {

    private final ExamService examService;
    private final ExamQuestionService examQuestionService;
    private final StatisticsService statisticsService;

    /**
     * 문제풀기 화면
     * @param examId
     * @param model
     * @return
     */
    @GetMapping("/solve")
    public String solve(@PathVariable("examId") Long examId, Model model) {
        ExamResponse exam = examService.get(examId);
        if (!SecurityUtils.isAdmin() && !exam.effectivelyActive()) {
            // 비관리자에게는 미사용 시험(또는 상위 과목/학과가 미사용)이 존재하지 않는 것처럼 처리
            throw new ExamNotFoundException(examId);
        }
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
                         @RequestParam(required = false) Integer elapsedSeconds,
                         HttpSession session,
                         Model model) {
        ExamResponse exam = examService.get(examId);
        List<ExamQuestionResponse> questions = examQuestionService.getQuestions(examId);
        List<String> answers = form.getAnswers();

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            String userAnswer = (answers != null && i < answers.size()) ? answers.get(i) : null;
            String correctAnswer = questions.get(i).answers();

            if (userAnswer != null && correctAnswer != null) {
                List<String> correctList = List.of(correctAnswer.split(","))
                        .stream()
                        .map(String::trim)
                        .toList();
                if (correctList.contains(userAnswer.trim())) {
                    score++;
                }
            }
        }

        // 문제 개수와 동일한 길이로 보정
        if (answers == null) answers = new ArrayList<>();
        while (answers.size() < questions.size()) {
            answers.add(null);
        }

        session.setAttribute("userAnswers_" + examId, answers);
        session.setAttribute("elapsedSeconds_" + examId, elapsedSeconds != null ? elapsedSeconds : 0);

        try {
            statisticsService.logAttempt(examId, exam.subjectId(), exam.subjectName(), exam.examType(),
                    exam.year(), score, questions.size(), elapsedSeconds);
        } catch (Exception e) {
            // 통계 로그 실패가 실제 채점/제출 흐름을 막으면 안 됨
            log.warn("응시 통계 로그 저장 실패 (examId={})", examId, e);
        }

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("score", score);
        model.addAttribute("elapsedSeconds", elapsedSeconds != null ? elapsedSeconds : 0);

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

        List<String> answers = (List<String>) session.getAttribute("userAnswers_" + examId);
        if (answers == null) {
            answers = new ArrayList<>();
        }

        int score = 0;
        List<List<String>> correctAnswersList = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            String userAnswer = (i < answers.size()) ? answers.get(i) : null;
            String correctAnswer = questions.get(i).answers();

            List<String> correctList = List.of(correctAnswer.split(","))
                    .stream()
                    .map(String::trim)
                    .toList();
            correctAnswersList.add(correctList);

            if (userAnswer != null && correctList.contains(userAnswer.trim())) {
                score++;
            }
        }

        while (answers.size() < questions.size()) {
            answers.add(null);
        }

        Integer elapsedSeconds = (Integer) session.getAttribute("elapsedSeconds_" + examId);

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("userAnswers", answers);
        model.addAttribute("correctAnswersList", correctAnswersList);
        model.addAttribute("score", score);
        model.addAttribute("elapsedSeconds", elapsedSeconds != null ? elapsedSeconds : 0);

        return "exam/review";
    }


}
