package knou.cbt.domain.subject.dto;

import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.subject.model.SubjectCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectResponse {
    private Long id;
    private String subjectName;
    private SubjectCategory subjectCategory;
    private Long departmentId;
    private String departmentName;
    private UseYn useYn;
    public static SubjectResponse of(SubjectDto dto) {
        return new SubjectResponse(
                dto.getId(),
                dto.getSubjectName(),
                dto.getSubjectCategory(),
                dto.getDepartmentId(),
                dto.getDepartmentName(),
                dto.getUseYn()
        );
    }
}