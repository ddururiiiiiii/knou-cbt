package knou.cbt.web.department;

import knou.cbt.domain.subject.dto.SubjectDto;
import knou.cbt.domain.subject.model.SubjectCategory;
import knou.cbt.domain.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 학과 REST API Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentRestController {

    private final SubjectService subjectService;

    /**
     * 특정 학과에 속한 과목 목록 조회
     */
    @GetMapping("/{deptId}/subjects")
    public List<SubjectDto> getSubjectsByDepartment(@PathVariable("deptId") Long deptId) {
        return subjectService.findByDepartmentId(deptId);
    }

    /**
     * 특정 학과에 존재하는 학년 목록 (과목 검색 드롭다운 단계용)
     */
    @GetMapping("/{deptId}/grades")
    public List<Integer> getGradesByDepartment(@PathVariable("deptId") Long deptId) {
        return subjectService.findGradesByDepartment(deptId);
    }

    /**
     * 특정 학과+학년에 존재하는 과목구분 목록 (과목 검색 드롭다운 단계용)
     */
    @GetMapping("/{deptId}/grades/{grade}/categories")
    public List<SubjectCategory> getCategoriesByDepartmentAndGrade(@PathVariable("deptId") Long deptId,
                                                                    @PathVariable("grade") Integer grade) {
        return subjectService.findCategoriesByDepartmentAndGrade(deptId, grade);
    }
}
