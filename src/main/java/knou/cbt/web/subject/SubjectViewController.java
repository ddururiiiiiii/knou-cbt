package knou.cbt.web.subject;

import jakarta.validation.Valid;
import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.department.dto.DepartmentUpdateRequest;
import knou.cbt.domain.department.exception.DuplicateDepartmentNameException;
import knou.cbt.domain.department.service.DepartmentService;
import knou.cbt.domain.subject.dto.SubjectRequest;
import knou.cbt.domain.subject.dto.SubjectResponse;
import knou.cbt.domain.subject.exception.DuplicateSubjectNameException;
import knou.cbt.domain.subject.model.SubjectCategory;
import knou.cbt.domain.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subjects")
public class SubjectViewController {

    private final SubjectService subjectService;
    private final DepartmentService departmentService;


    /**
     * 과목 전체 조회 (페이징 처리)
     *
     * @param pageRequest
     * @param keyword
     * @param useYn
     * @param model
     * @return
     */
    @GetMapping
    public String list(@Valid PageRequest pageRequest,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String useYn,
                       Model model) {
        PageResponse<SubjectResponse> pageResponse = subjectService.listPage(keyword, useYn, pageRequest);

        model.addAttribute("pagination", pageResponse);
        model.addAttribute("keyword", keyword);
        model.addAttribute("useYn", useYn);

        return "subject/subjectList";
    }

    /**
     * 과목 등록 화면
     * @param model
     * @return
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        setupFormModel(model,
                "과목 등록",
                "/subjects",
                "post",
                "create",
                new SubjectRequest()
                , null);
        return "subject/subjectForm";
    }

    /**
     * 과목 등록 처리
     * @param req
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("subject") SubjectRequest req,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            setupFormModel(model, "과목 등록", "/subjects", "post", "create", req, null);
            return "subject/subjectForm";
        }
        try {
            subjectService.create(req);
        } catch (DuplicateSubjectNameException e) {
            bindingResult.rejectValue("subjectName", "duplicate", e.getMessage());
            setupFormModel(model, "과목 등록", "/subjects", "post", "create", req, null);
            return "subject/subjectForm";
        }
        return "redirect:/subjects";
    }

    /**
     * 과목 수정 화면
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        SubjectResponse subjectResponse = subjectService.get(id);

        setupFormModel(model,
                "과목 수정",
                "/subjects/" + id,
                "patch",
                "edit",
                subjectResponse,
                id);
        model.addAttribute("subjectId", id);
        return "subject/subjectForm";
    }

    /**
     * 과목 수정 처리
     * @param id
     * @param req
     * @param bindingResult
     * @param model
     * @return
     */
    @PatchMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("subject") SubjectRequest req,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            setupFormModel(model, "과목 수정", "/subjects/" + id, "patch", "edit", req, id);
            return "subject/subjectForm";
        }
        try {
            subjectService.update(id, req);
        } catch (DuplicateSubjectNameException e) {
            bindingResult.rejectValue("subjectName", "duplicate", e.getMessage());
            setupFormModel(model, "과목 수정", "/subjects/" + id, "patch", "edit", req, id);
            model.addAttribute("subjectId", id);
            return "subject/subjectForm";
        }

        return "redirect:/subjects";
    }

    /**
     * 과목 삭제 처리
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        subjectService.delete(id);
        return "redirect:/subjects";
    }

    // 공통 Form 세팅 메서드
    private void setupFormModel(Model model,
                                String formTitle,
                                String actionUrl,
                                String method,
                                String mode,
                                Object req,
                                Long SubjectId) {
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("actionUrl", actionUrl);
        model.addAttribute("method", method);
        model.addAttribute("mode", mode);
        model.addAttribute("subject", req);
        model.addAttribute("categories", SubjectCategory.values());
        model.addAttribute("departments", departmentService.findAll());
        if (SubjectId != null) {
            model.addAttribute("subjectId", SubjectId);
        }
    }
}
