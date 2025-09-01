package knou.cbt.domain.subject.dto;

import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.subject.model.SubjectCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectDto {
    private Long id;
    private String subjectName;
    private SubjectCategory subjectCategory;
    private Long departmentId;
    private String departmentName;
    private UseYn useYn;
}