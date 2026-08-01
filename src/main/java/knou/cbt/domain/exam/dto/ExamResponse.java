package knou.cbt.domain.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.department.dto.DepartmentResponse;
import knou.cbt.domain.department.model.Department;
import knou.cbt.domain.exam.model.ExamType;
import knou.cbt.domain.subject.model.GradeCsv;
import knou.cbt.domain.subject.model.Semester;

import java.time.LocalDateTime;

public record ExamResponse(
        Long id,
        Long subjectId,
        String subjectName,
        Long departmentId,
        String departmentName,
        ExamType examType,
        String grade, // 콤마구분 다중 학년, 예: "1,3" (gradeDisplay()로 화면 표시용 문자열 사용)
        Semester semester,
        Integer year,
        UseYn useYn,
        boolean hasExamQuestions,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt,
        boolean effectivelyActive
) {
    public String gradeDisplay() {
        return GradeCsv.toDisplay(grade);
    }

    public static ExamResponse of(ExamDto exam, boolean hasExamQuestions) {
        return new ExamResponse(
                exam.getId(),
                exam.getSubjectId(),
                exam.getSubjectName(),
                exam.getDepartmentId(),
                exam.getDepartmentName(),
                exam.getExamType(),
                exam.getGrade(),
                exam.getSemester(),
                exam.getYear(),
                exam.getUseYn(),
                hasExamQuestions,
                exam.getCreatedAt(),
                exam.getUpdatedAt(),
                UseYn.Y.equals(exam.getUseYn())
                        && UseYn.Y.equals(exam.getSubjectUseYn())
                        && UseYn.Y.equals(exam.getDepartmentUseYn())
        );
    }
    public static ExamResponse of(ExamDto exam) {
        return of(exam, false);
    }
}

