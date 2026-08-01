package knou.cbt.domain.subject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.subject.model.GradeCsv;
import knou.cbt.domain.subject.model.Semester;
import knou.cbt.domain.subject.model.SubjectCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectResponse {
    private Long id;
    private String subjectName;
    private SubjectCategory subjectCategory;
    private String grade; // 콤마구분 다중 학년, 예: "1,3"
    private Semester semester;
    private Long departmentId;
    private String departmentName;
    private UseYn useYn;
    boolean hasExams;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private boolean effectivelyActive;

    public List<Integer> getGrades() {
        return GradeCsv.fromCsv(grade);
    }

    public String getGradeDisplay() {
        return GradeCsv.toDisplay(grade);
    }

    public static SubjectResponse of(SubjectDto dto, boolean hasExams) {
        return new SubjectResponse(
                dto.getId(),
                dto.getSubjectName(),
                dto.getSubjectCategory(),
                dto.getGrade(),
                dto.getSemester(),
                dto.getDepartmentId(),
                dto.getDepartmentName(),
                dto.getUseYn(),
                hasExams,
                dto.getCreatedAt(),
                dto.getUpdatedAt(),
                UseYn.Y.equals(dto.getUseYn()) && UseYn.Y.equals(dto.getDepartmentUseYn())
        );
    }
    public static SubjectResponse of(SubjectDto dto) {
        return of(dto, false);
    }
}