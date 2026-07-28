package knou.cbt.domain.subject.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.subject.dto.SubjectDto;
import knou.cbt.domain.subject.dto.SubjectRequest;
import knou.cbt.domain.subject.dto.SubjectResponse;
import knou.cbt.domain.subject.model.SubjectCategory;

import java.util.List;

public interface SubjectService {

    List<SubjectDto> findAll();

    PageResponse<SubjectResponse> listPage(
            String keyword,
            String useYn,
            Long departmentId,
            Integer grade,
            SubjectCategory subjectCategory,
            PageRequest pageRequest
    );

    int count(String keyword, String useYn);

    List<SubjectDto> findTopSubjectsByExamCount(int limit);

    List<SubjectDto> findByDepartmentId(Long deptId);

    List<Integer> findGradesByDepartment(Long departmentId);

    List<SubjectCategory> findCategoriesByDepartmentAndGrade(Long departmentId, Integer grade);

    SubjectResponse get(Long id);

    void create(SubjectRequest request);

    void update(Long id, SubjectRequest request);

    void delete(Long id);
}
