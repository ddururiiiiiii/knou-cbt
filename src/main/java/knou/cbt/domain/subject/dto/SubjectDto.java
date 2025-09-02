package knou.cbt.domain.subject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
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
    private Integer grade;
    private Long departmentId;
    private String departmentName;
    private UseYn useYn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}