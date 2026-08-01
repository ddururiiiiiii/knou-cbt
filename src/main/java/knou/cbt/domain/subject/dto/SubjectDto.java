package knou.cbt.domain.subject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.subject.model.GradeCsv;
import knou.cbt.domain.subject.model.Semester;
import knou.cbt.domain.subject.model.SubjectCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SubjectDto {
    private Long id;
    private String subjectName;
    private SubjectCategory subjectCategory;
    private String grade; // 콤마구분 다중 학년, 예: "1,3" (GradeCsv 참고)
    private Semester semester;
    private Long departmentId;
    private String departmentName;
    private UseYn departmentUseYn;
    private UseYn useYn;
    private Integer examCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public String getGradeDisplay() {
        return GradeCsv.toDisplay(grade);
    }

    public String getSemesterDescription() {
        return semester != null ? semester.getDescription() : null;
    }
}