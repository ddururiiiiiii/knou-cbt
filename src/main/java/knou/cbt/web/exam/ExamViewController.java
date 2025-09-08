package knou.cbt.web.exam;

import jakarta.validation.Valid;
import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.department.service.DepartmentService;
import knou.cbt.domain.exam.dto.ExamResponse;
import knou.cbt.domain.exam.model.ExamType;
import knou.cbt.domain.exam.service.ExamService;
import knou.cbt.domain.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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


}
