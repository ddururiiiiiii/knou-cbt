package knou.cbt.web.exam;

import jakarta.validation.Valid;
import knou.cbt.domain.department.service.DepartmentService;
import knou.cbt.domain.exam.dto.AnswerForm;
import knou.cbt.domain.exam.dto.ExamRequest;
import knou.cbt.domain.exam.dto.ExamResponse;
import knou.cbt.domain.exam.dto.mapper.ExamDtoMapper;
import knou.cbt.domain.exam.exception.DuplicateExamException;
import knou.cbt.domain.exam.model.ExamType;
import knou.cbt.domain.exam.service.ExamService;
import knou.cbt.domain.examquestion.dto.ExamQuestionResponse;
import knou.cbt.domain.examquestion.service.ExamQuestionService;
import knou.cbt.domain.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/exams")
public class AdminExamViewController {

    private final ExamService examService;
    private final SubjectService subjectService;
    private final DepartmentService departmentService;
    private final ExamQuestionService examQuestionService;

    /**
     * 시험 등록 화면
     * @param model
     * @return
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        setupFormModel(model,
                "시험 등록",
                "/admin/exams",
                "post",
                "create",
                new ExamRequest(),
                null);
        return "admin/exam/examForm";
    }

    /**
     * 시험 등록 처리
     * @param req
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("exam") ExamRequest req,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            setupFormModel(model, "시험 등록", "/admin/exams", "post", "create", req, null);
            return "admin/exam/examForm";
        }
        try {
            examService.create(req);
        } catch (DuplicateExamException e) {
            bindingResult.reject("duplicate", e.getMessage());
            setupFormModel(model, "시험 등록", "/admin/exams", "post", "create", req, null);
            return "admin/exam/examForm";
        }
        return "redirect:/exams";
    }

    /**
     * 시험 수정 화면
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        ExamResponse examResponse = examService.get(id);
        // Response → Request 변환
        ExamRequest examRequest = ExamDtoMapper.toRequest(examResponse);
        setupFormModel(model,
                "시험 수정",
                "/admin/exams/" + id,
                "patch",
                "edit",
                examRequest,
                id);
        return "admin/exam/examForm";
    }

    /**
     * 시험 수정 처리
     * @param id
     * @param req
     * @param bindingResult
     * @param model
     * @return
     */
    @PatchMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("exam") ExamRequest req,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            setupFormModel(model, "시험 수정", "/admin/exams/" + id, "patch", "edit", req, id);
            return "admin/exam/examForm";
        }
        try {
            examService.update(id, req);
        } catch (DuplicateExamException e) {
            bindingResult.reject("duplicate", e.getMessage());
            setupFormModel(model, "시험 수정", "/admin/exams/" + id, "patch", "edit", req, id);
            return "admin/exam/examForm";
        }

        return "redirect:/exams";
    }

    /**
     * 시험 삭제 처리
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        examService.delete(id);
        return "redirect:/exams";
    }

    // 공통 Form 세팅 메서드
    private void setupFormModel(Model model,
                                String formTitle,
                                String actionUrl,
                                String method,
                                String mode,
                                ExamRequest req,
                                Long examId) {
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("actionUrl", actionUrl);
        model.addAttribute("method", method);
        model.addAttribute("mode", mode);
        model.addAttribute("exam", req);

        // 시험 선택 옵션들
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("examTypes", ExamType.values());

        if (examId != null) {
            model.addAttribute("examId", examId);
        }
    }

    /**
     * 시험 미리보기
     * @param examId
     * @param model
     * @return
     */
    @GetMapping("/{examId}/preview")
    public String preview(@PathVariable("examId") Long examId, Model model) {
        ExamResponse exam = examService.get(examId);
        List<ExamQuestionResponse> questions = examQuestionService.getQuestions(examId);

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("answerForm", new AnswerForm());
        return "admin/exam/preview";
    }
}
