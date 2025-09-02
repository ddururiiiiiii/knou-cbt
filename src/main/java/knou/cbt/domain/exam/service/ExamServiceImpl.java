package knou.cbt.domain.exam.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.exam.dto.ExamDto;
import knou.cbt.domain.exam.dto.ExamRequest;
import knou.cbt.domain.exam.dto.ExamResponse;
import knou.cbt.domain.exam.dto.mapper.ExamDtoMapper;
import knou.cbt.domain.exam.exception.DuplicateExamException;
import knou.cbt.domain.exam.exception.ExamNotFoundException;
import knou.cbt.domain.exam.mapper.ExamMapper;
import knou.cbt.domain.exam.model.Exam;
import knou.cbt.domain.exam.model.ExamType;
import knou.cbt.domain.subject.dto.SubjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExamResponse> listPage(Long departmentId,
                                               Long subjectId,
                                               ExamType examType,
                                               Integer year,
                                               String useYn,
                                               PageRequest pageRequest) {

        List<ExamResponse> content = mapper.findAllExamDetails(
                        pageRequest.offset(),
                        pageRequest.sizeOrDefault(),
                        useYn,
                        departmentId,
                        subjectId,
                        examType,
                        year
                ).stream()
                .map(ExamResponse::of)
                .toList();

        int total = mapper.countAll(useYn, departmentId, subjectId, examType, year);
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
    public int count(String useYn,
                     Long departmentId,
                     Long subjectId,
                     ExamType examType,
                     Integer year) {
        return mapper.countAll(useYn, departmentId, subjectId, examType, year);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponse get(Long id) {
        return findExamOrThrow(id);
    }
    @Override
    @Transactional
    public void create(ExamRequest request) {
        checkDuplicate(request, null);
        Exam exam = ExamDtoMapper.fromCreateRequest(request);
        mapper.insert(exam);
    }

    @Override
    public void update(Long id, ExamRequest request) {
        findExamOrThrow(id);
        checkDuplicate(request, id);
        Exam exam = ExamDtoMapper.fromUpdateRequest(id, request);
        mapper.update(exam);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ExamDto existing = mapper.findExamExtendedById(id);
        if (existing == null) {
            throw new ExamNotFoundException(id);
        }
        mapper.delete(id);
    }

    private void checkDuplicate(ExamRequest request, Long currentId) {
        ExamDto duplicate = mapper.findDuplicate(
                request.getSubjectId(),
                request.getExamType(),
                request.getYear()
        );

        if (duplicate != null && (currentId == null || !duplicate.getId().equals(currentId))) {
            throw new DuplicateExamException(duplicate.getSubjectName(), request.getExamType().getDescription(), request.getYear());
        }
    }

    private ExamResponse findExamOrThrow(Long id) {
        ExamDto dto = mapper.findExamExtendedById(id);
        if (dto == null) {
            throw new ExamNotFoundException(id);
        }
        return ExamResponse.of(dto); // DTO → Response 변환
    }
}
