package knou.cbt.web.department;

import knou.cbt.domain.subject.dto.SubjectDto;
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
}