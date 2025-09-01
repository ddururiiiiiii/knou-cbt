package knou.cbt.domain.subject.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.subject.dto.SubjectRequest;
import knou.cbt.domain.subject.dto.SubjectResponse;
import knou.cbt.domain.subject.dto.mapper.SubjectDtoMapper;
import knou.cbt.domain.subject.exception.DuplicateSubjectNameException;
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
        .map(row -> SubjectResponse.of(row))
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
        mapper.delete(id);
    }

    private void validateDuplicateName(String name, Long excludeId) {
        Subject existing = mapper.findByNameWithDepartment(name);
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw new DuplicateSubjectNameException(name);
        }
    }

    private SubjectResponse findSubjectOrThrow(Long id) {
        SubjectResponse subject = mapper.findByIdWithDepartment(id);
        if (subject == null) {
            throw new SubjectNotFoundException(id);
        }
        return subject;
    }
}
