package knou.cbt.web.exam;

import jakarta.validation.Valid;
import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.department.service.DepartmentService;
import knou.cbt.domain.exam.dto.ExamRequest;
import knou.cbt.domain.exam.dto.ExamResponse;
import knou.cbt.domain.exam.dto.mapper.ExamDtoMapper;
import knou.cbt.domain.exam.exception.DuplicateExamException;
import knou.cbt.domain.exam.service.ExamService;
import knou.cbt.domain.exam.model.ExamType;
import knou.cbt.domain.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/exams")
public class ExamViewController {

    private final ExamService examService;
    private final SubjectService subjectService;
    private final DepartmentService departmentService;

    /**
     * 시험 전체 조회 (페이징 처리)
     * @param pageRequest
     * @param departmentId
     * @param subjectId
     * @param examType
     * @param year
     * @param useYn
     * @param model
     * @return
     */
    @GetMapping
    public String list(@Valid PageRequest pageRequest,
                       @RequestParam(required = false) Long departmentId,
                       @RequestParam(required = false) Long subjectId,
                       @RequestParam(required = false) ExamType examType,
                       @RequestParam(required = false) Integer year,
                       @RequestParam(required = false) String useYn,
                       Model model) {

        PageResponse<ExamResponse> pageResponse =
                examService.listPage(departmentId, subjectId, examType, year, useYn, pageRequest);

        model.addAttribute("pagination", pageResponse);

        // 검색조건 유지
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("subjectId", subjectId);
        model.addAttribute("examType", examType);
        model.addAttribute("year", year);
        model.addAttribute("useYn", useYn);

        // 옵션 목록
        model.addAttribute("departments", departmentService.findAll());
        if (departmentId != null) {
            // 특정 학과가 선택된 경우 → 해당 학과 과목만 조회
            model.addAttribute("subjects", subjectService.findByDepartmentId(departmentId));
        } else {
            // 학과 선택 안된 경우 → 빈 리스트 내려줌
            model.addAttribute("subjects", List.of());
        }
        model.addAttribute("examTypes", ExamType.values());

        return "exam/examList";
    }

    /**
     * 시험 등록 화면
     * @param model
     * @return
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        setupFormModel(model,
                "시험 등록",
                "/exams",
                "post",
                "create",
                new ExamRequest(),
                null);
        return "exam/examForm";
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
            setupFormModel(model, "시험 등록", "/exams", "post", "create", req, null);
            return "exam/examForm";
        }
        try {
            examService.create(req);
        } catch (DuplicateExamException e) {
            bindingResult.reject("duplicate", e.getMessage());
            setupFormModel(model, "시험 등록", "/exams", "post", "create", req, null);
            return "exam/examForm";
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
                "/exams/" + id,
                "patch",
                "edit",
                examRequest,
                id);
        return "exam/examForm";
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
            setupFormModel(model, "시험 수정", "/exams/" + id, "patch", "edit", req, id);
            return "exam/examForm";
        }
        try {
            examService.update(id, req);
        } catch (DuplicateExamException e) {
            bindingResult.reject("duplicate", e.getMessage());
            setupFormModel(model, "시험 수정", "/exams/" + id, "patch", "edit", req, id);
            return "exam/examForm";
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
}
