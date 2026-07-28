package knou.cbt.web.subject;

import knou.cbt.domain.exam.model.ExamType;
import knou.cbt.domain.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 과목 기준 시험 검색 드롭다운(구분/년도)용 REST API
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subjects")
public class SubjectRestController {

    private final ExamService examService;

    @GetMapping("/{subjectId}/exam-types")
    public List<ExamType> getExamTypes(@PathVariable("subjectId") Long subjectId) {
        return examService.findExamTypesBySubject(subjectId);
    }

    @GetMapping("/{subjectId}/exam-types/{examType}/years")
    public List<Integer> getYears(@PathVariable("subjectId") Long subjectId,
                                   @PathVariable("examType") ExamType examType) {
        return examService.findYearsBySubjectAndType(subjectId, examType);
    }
}
