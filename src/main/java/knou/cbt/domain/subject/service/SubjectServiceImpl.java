package knou.cbt.domain.subject.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.exam.mapper.ExamMapper;
import knou.cbt.domain.subject.dto.SubjectDto;
import knou.cbt.domain.subject.dto.SubjectRequest;
import knou.cbt.domain.subject.dto.SubjectResponse;
import knou.cbt.domain.subject.dto.mapper.SubjectDtoMapper;
import knou.cbt.domain.subject.exception.DuplicateSubjectNameException;
import knou.cbt.domain.subject.exception.SubjectDeleteNotAllowedException;
import knou.cbt.domain.subject.exception.SubjectNotFoundException;
import knou.cbt.domain.subject.mapper.SubjectMapper;
import knou.cbt.domain.subject.model.Semester;
import knou.cbt.domain.subject.model.Subject;
import knou.cbt.domain.subject.model.SubjectCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectServiceImpl implements SubjectService{

    private final SubjectMapper mapper;
    private final ExamMapper examMapper;

    @Override
    public List<SubjectDto> findAll() {
        return mapper.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SubjectResponse> listPage(
            String keyword,
            String useYn,
            Long departmentId,
            Integer grade,
            SubjectCategory subjectCategory,
            PageRequest pageRequest
    ) {
        List<SubjectDto> subjects = mapper.findAllWithDepartment(
                pageRequest.offset(),
                pageRequest.sizeOrDefault(),
                keyword,
                useYn,
                departmentId,
                grade,
                subjectCategory
        );

        List<Long> subjectIds = subjects.stream().map(SubjectDto::getId).toList();
        Set<Long> subjectIdsWithExams = subjectIds.isEmpty()
                ? Set.of()
                : examMapper.findSubjectIdsWithExams(subjectIds);

        List<SubjectResponse> content = subjects.stream()
                .map(dto -> SubjectResponse.of(dto, subjectIdsWithExams.contains(dto.getId())))
                .toList();

        int total = mapper.countAll(keyword, useYn, departmentId, grade, subjectCategory);
        int totalPages = (int) Math.ceil((double) total / pageRequest.sizeOrDefault());

        return new PageResponse<>(
                content,
                pageRequest.pageOrDefault(),
                pageRequest.sizeOrDefault(),
                total,
                totalPages
        );
    }


    @Override
    @Transactional(readOnly = true)
    public int count(String keyword, String useYn) {
        return mapper.countAll(keyword, useYn, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDto> findTopSubjectsByExamCount(int limit) {
        return mapper.findTopSubjectsByExamCount(limit);
    }

    @Override
    public SubjectResponse get(Long id) {
        return findSubjectOrThrow(id);
    }

    @Override
    public List<SubjectDto> findByDepartmentId(Long deptId) {
        return mapper.findByDepartmentId(deptId);
    }

    @Override
    public List<Integer> findGradesByDepartment(Long departmentId) {
        return mapper.findGradesByDepartment(departmentId);
    }

    @Override
    public List<SubjectCategory> findCategoriesByDepartmentAndGrade(Long departmentId, Integer grade) {
        return mapper.findCategoriesByDepartmentAndGrade(departmentId, grade);
    }

    @Override
    public void create(SubjectRequest request) {
        validateDuplicateName(request.getSubjectName(), request.getDepartmentId(), request.getSemester(), null);
        Subject subject = SubjectDtoMapper.fromCreateRequest(request);
        mapper.insert(subject);
    }

    @Override
    public void update(Long id, SubjectRequest request) {
        findSubjectOrThrow(id);
        validateDuplicateName(request.getSubjectName(), request.getDepartmentId(), request.getSemester(), id);
        Subject subject = SubjectDtoMapper.fromUpdateRequest(id, request);
        mapper.update(subject);
    }
    @Override
    public void delete(Long id) {
        findSubjectOrThrow(id);
        validateNoExams(id);
        mapper.delete(id);
    }

    private void validateDuplicateName(String name, Long departmentId, Semester semester, Long excludeId) {
        SubjectDto existing = mapper.findByNameDeptSemester(name, departmentId, semester);
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw new DuplicateSubjectNameException(name);
        }
    }

    private SubjectResponse findSubjectOrThrow(Long id) {
        SubjectDto dto = mapper.findByIdWithDepartment(id);
        if (dto == null) {
            throw new SubjectNotFoundException(id);
        }
        return SubjectResponse.of(dto); // DTO → Response 변환
    }

    private void validateNoExams(Long subjectId) {
        if (examMapper.existsBySubjectId(subjectId)) {
            throw new SubjectDeleteNotAllowedException(subjectId);
        }
    }
}
