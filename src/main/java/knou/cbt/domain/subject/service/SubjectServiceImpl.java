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
import knou.cbt.domain.subject.model.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            PageRequest pageRequest
    ) {
        List<SubjectResponse> content = mapper.findAllWithDepartment(
                        pageRequest.offset(),
                        pageRequest.sizeOrDefault(),
                        keyword,
                        useYn
                ).stream()
                .map(dto -> {
                    boolean hasExams = examMapper.existsBySubjectId(dto.getId());
                    return SubjectResponse.of(dto, hasExams);
                })
                .toList();

        int total = mapper.countAll(keyword, useYn);
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
        return mapper.countAll(keyword, useYn);
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
    public void create(SubjectRequest request) {
        validateDuplicateName(request.getSubjectName(), null);
        Subject subject = SubjectDtoMapper.fromCreateRequest(request);
        mapper.insert(subject);
    }

    @Override
    public void update(Long id, SubjectRequest request) {
        findSubjectOrThrow(id);
        validateDuplicateName(request.getSubjectName(), id);
        Subject subject = SubjectDtoMapper.fromUpdateRequest(id, request);
        mapper.update(subject);
    }
    @Override
    public void delete(Long id) {
        findSubjectOrThrow(id);
        validateNoExams(id);
        mapper.delete(id);
    }

    private void validateDuplicateName(String name, Long excludeId) {
        SubjectDto existing = mapper.findByNameWithDepartment(name);
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
