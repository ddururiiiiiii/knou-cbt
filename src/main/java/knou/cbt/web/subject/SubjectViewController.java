package knou.cbt.web.subject;

import jakarta.validation.Valid;
import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.department.dto.DepartmentResponse;
import knou.cbt.domain.department.service.DepartmentService;
import knou.cbt.domain.subject.dto.SubjectRequest;
import knou.cbt.domain.subject.dto.SubjectResponse;
import knou.cbt.domain.subject.exception.DuplicateSubjectNameException;
import knou.cbt.domain.subject.model.Semester;
import knou.cbt.domain.subject.model.SubjectCategory;
import knou.cbt.domain.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/subjects")
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
    public String list(PageRequest pageRequest,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String useYn,
                       @RequestParam(required = false) Long departmentId,
                       @RequestParam(required = false) Integer grade,
                       @RequestParam(required = false) SubjectCategory subjectCategory,
                       Model model) {
        PageResponse<SubjectResponse> pageResponse =
                subjectService.listPage(keyword, useYn, departmentId, grade, subjectCategory, pageRequest);

        model.addAttribute("pagination", pageResponse);
        model.addAttribute("keyword", keyword);
        model.addAttribute("useYn", useYn);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("grade", grade);
        model.addAttribute("subjectCategory", subjectCategory);
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("categories", SubjectCategory.values());

        return "admin/subject/subjectList";
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
                "/admin/subjects",
                "post",
                "create",
                new SubjectRequest()
                , null, null);
        return "admin/subject/subjectForm";
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
            setupFormModel(model, "과목 등록", "/admin/subjects", "post", "create", req, null, null);
            return "admin/subject/subjectForm";
        }
        try {
            subjectService.create(req);
        } catch (DuplicateSubjectNameException e) {
            bindingResult.rejectValue("subjectName", "duplicate", e.getMessage());
            setupFormModel(model, "과목 등록", "/admin/subjects", "post", "create", req, null, null);
            return "admin/subject/subjectForm";
        }
        return "redirect:/admin/subjects";
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
                "/admin/subjects/" + id,
                "patch",
                "edit",
                subjectResponse,
                id,
                subjectResponse.getDepartmentId());
        model.addAttribute("subjectId", id);
        return "admin/subject/subjectForm";
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
        // 현재 DB에 저장된 학과(비활성 상태라 목록엔 안 나올 수 있음)를 드롭다운에 포함시키기 위해 조회.
        // 제출된 req.getDepartmentId()는 검증 실패 시 0 등 유효하지 않은 값일 수 있어 신뢰하지 않는다.
        Long currentDepartmentId = subjectService.get(id).getDepartmentId();

        if (bindingResult.hasErrors()) {
            setupFormModel(model, "과목 수정", "/admin/subjects/" + id, "patch", "edit", req, id, currentDepartmentId);
            return "admin/subject/subjectForm";
        }
        try {
            subjectService.update(id, req);
        } catch (DuplicateSubjectNameException e) {
            bindingResult.rejectValue("subjectName", "duplicate", e.getMessage());
            setupFormModel(model, "과목 수정", "/admin/subjects/" + id, "patch", "edit", req, id, currentDepartmentId);
            model.addAttribute("subjectId", id);
            return "admin/subject/subjectForm";
        }

        return "redirect:/admin/subjects";
    }

    /**
     * 과목 삭제 처리
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        subjectService.delete(id);
        return "redirect:/admin/subjects";
    }

    // 공통 Form 세팅 메서드
    private void setupFormModel(Model model,
                                String formTitle,
                                String actionUrl,
                                String method,
                                String mode,
                                Object req,
                                Long SubjectId,
                                Long currentDepartmentId) {
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("actionUrl", actionUrl);
        model.addAttribute("method", method);
        model.addAttribute("mode", mode);
        model.addAttribute("subject", req);
        model.addAttribute("categories", SubjectCategory.values());
        model.addAttribute("semesters", Semester.values());
        model.addAttribute("departments", resolveDepartments(currentDepartmentId));
        if (SubjectId != null) {
            model.addAttribute("subjectId", SubjectId);
        }
    }

    /**
     * 학과 드롭다운 목록을 구성한다. 활성(사용중) 학과 목록을 기본으로 하되, 수정 중인 과목이
     * 속한 학과가 비활성 상태라 목록에서 빠졌다면 그 학과도 포함시켜, 다른 필드만 고쳐 저장할 때
     * 학과 선택값이 의도치 않게 "학과 선택"(미지정)으로 바뀌어 저장이 막히는 것을 방지한다.
     */
    private List<DepartmentResponse> resolveDepartments(Long currentDepartmentId) {
        List<DepartmentResponse> departments = new ArrayList<>(departmentService.findAll());
        if (currentDepartmentId != null && departments.stream().noneMatch(d -> d.id().equals(currentDepartmentId))) {
            departments.add(departmentService.get(currentDepartmentId));
        }
        return departments;
    }
}
