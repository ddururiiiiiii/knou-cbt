package knou.cbt.domain.exam.dto.mapper;

import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.exam.dto.ExamRequest;
import knou.cbt.domain.exam.dto.ExamResponse;
import knou.cbt.domain.exam.model.Exam;

public class ExamDtoMapper {

    private ExamDtoMapper() {
        // 유틸 클래스이므로 인스턴스화 방지
    }

    // DTO -> Entity
    public static Exam fromCreateRequest(ExamRequest req) {
        return Exam.create(
                null,
                req.getSubjectId(),
                req.getExamType(),
                req.getYear(),
                UseYn.Y
        );
    }

    // DTO -> Entity
    public static Exam fromUpdateRequest(Long id, ExamRequest req) {
        return Exam.create(
                id,
                req.getSubjectId(),
                req.getExamType(),
                req.getYear(),
                req.getUseYn()
        );
    }
    public static ExamRequest toRequest(ExamResponse response) {
        ExamRequest req = new ExamRequest();
        req.setSubjectId(response.subjectId());
        req.setExamType(response.examType());
        req.setYear(response.year());
        req.setUseYn(response.useYn());
        return req;
    }
}
