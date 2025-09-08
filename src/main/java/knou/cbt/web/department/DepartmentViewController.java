package knou.cbt.web.department;

import jakarta.validation.Valid;
import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.department.dto.DepartmentCreateRequest;
import knou.cbt.domain.department.dto.DepartmentResponse;
import knou.cbt.domain.department.dto.DepartmentUpdateRequest;
import knou.cbt.domain.department.dto.mapper.DepartmentDtoMapper;
import knou.cbt.domain.department.exception.DuplicateDepartmentNameException;
import knou.cbt.domain.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/departments")
public class DepartmentViewController {

    private final DepartmentService service;

    /**
     * 학과 전체 조회 (페이징 처리)
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

        PageResponse<DepartmentResponse> pageResponse = service.listPage(keyword, useYn, pageRequest);

        model.addAttribute("pagination", pageResponse);
        model.addAttribute("keyword", keyword);
        model.addAttribute("useYn", useYn);

        return "admin/department/departmentList";
    }

    /**
     * 학과 등록 화면
     * @param model
     * @return
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        setupFormModel(model,
                "학과 등록",
                "/admin/departments",
                "post",
                "create",
                new DepartmentCreateRequest()
        , null);
        return "admin/department/departmentForm";
    }

    /**
     * 학과 등록 처리
     * @param req
     * @param bindingResult
     * @return
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("department") DepartmentCreateRequest req,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            setupFormModel(model, "학과 등록", "/admin/departments", "post", "create", req, null);
            return "admin/department/departmentForm";
        }
        try {
            service.create(req);
        } catch (DuplicateDepartmentNameException e) {
            bindingResult.rejectValue("departmentName", "duplicate", e.getMessage());
            setupFormModel(model, "학과 등록", "/admin/departments", "post", "create", req, null);
            return "admin/department/departmentForm";
        }
        return "redirect:/admin/departments";
    }

    /**
     * 학과 수정 화면
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        DepartmentResponse dept = service.get(id);
        DepartmentUpdateRequest req = DepartmentDtoMapper.toUpdateRequest(dept);

        setupFormModel(model,
                "학과 수정",
                "/admin/departments/" + id,
                "patch",
                "edit",
                req,
                id);
        model.addAttribute("departmentId", id);
        return "admin/department/departmentForm";
    }


    /**
     * 학과 수정 처리
     * @param id
     * @param req
     * @param bindingResult
     * @return
     */
    @PatchMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("department") DepartmentUpdateRequest req,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            setupFormModel(model, "학과 수정", "/admin/departments/" + id, "patch", "edit", req, id);
            return "admin/department/departmentForm";
        }
        try {
            service.update(id, req);
        } catch (DuplicateDepartmentNameException e) {
            bindingResult.rejectValue("departmentName", "duplicate", e.getMessage());
            setupFormModel(model, "학과 수정", "/admin/departments/" + id, "patch", "edit", req, id);
            model.addAttribute("departmentId", id);
            return "admin/department/departmentForm";
        }

        return "redirect:/admin/departments";
    }

    /**
     * 학과 삭제 처리
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        service.delete(id);
        return "redirect:/admin/departments";
    }

    // 공통 Form 세팅 메서드
    private void setupFormModel(Model model,
                                String formTitle,
                                String actionUrl,
                                String method,
                                String mode,
                                Object req,
                                Long departmentId) {
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("actionUrl", actionUrl);
        model.addAttribute("method", method);
        model.addAttribute("mode", mode);
        model.addAttribute("department", req);
        model.addAttribute("useYnValues", UseYn.values());
        if (departmentId != null) {
            model.addAttribute("departmentId", departmentId);
        }
    }
}