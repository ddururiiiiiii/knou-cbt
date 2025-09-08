package knou.cbt.domain.subject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.subject.model.SubjectCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectResponse {
    private Long id;
    private String subjectName;
    private SubjectCategory subjectCategory;
    private Integer grade;
    private Long departmentId;
    private String departmentName;
    private UseYn useYn;
    boolean hasExams;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static SubjectResponse of(SubjectDto dto, boolean hasExams) {
        return new SubjectResponse(
                dto.getId(),
                dto.getSubjectName(),
                dto.getSubjectCategory(),
                dto.getGrade(),
                dto.getDepartmentId(),
                dto.getDepartmentName(),
                dto.getUseYn(),
                hasExams,
                dto.getCreatedAt(),
                dto.getUpdatedAt()
        );
    }
    public static SubjectResponse of(SubjectDto dto) {
        return of(dto, false);
    }
}