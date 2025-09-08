package knou.cbt.domain.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.department.dto.DepartmentResponse;
import knou.cbt.domain.department.model.Department;
import knou.cbt.domain.exam.model.ExamType;

import java.time.LocalDateTime;

public record ExamResponse(
        Long id,
        Long subjectId,
        String subjectName,
        Long departmentId,
        String departmentName,
        ExamType examType,
        Integer grade,
        Integer year,
        UseYn useYn,
        boolean hasExamQuestions,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
    public static ExamResponse of(ExamDto exam, boolean hasExamQuestions) {
        return new ExamResponse(
                exam.getId(),
                exam.getSubjectId(),
                exam.getSubjectName(),
                exam.getDepartmentId(),
                exam.getDepartmentName(),
                exam.getExamType(),
                exam.getGrade(),
                exam.getYear(),
                exam.getUseYn(),
                hasExamQuestions,
                exam.getCreatedAt(),
                exam.getUpdatedAt()
        );
    }
    public static ExamResponse of(ExamDto exam) {
        return of(exam, false);
    }
}

