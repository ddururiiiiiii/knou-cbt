package knou.cbt.domain.subject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.subject.model.SubjectCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class SubjectRequest {

    @NotBlank(message = "과목명은 필수 입력 값입니다.")
    @Size(max = 50, message = "과목명은 최대 50자까지 가능합니다.")
    private String subjectName;

    @NotNull(message = "과목 구분은 필수 입력 값입니다.")
    private SubjectCategory subjectCategory;

    @NotNull(message = "학년 선택은 필수 입력 값입니다.")
    @Min(value = 1, message = "학년 선택은 필수 입력 값입니다.")
    private Integer grade;

    @NotNull(message = "학과 선택은 필수 입력 값입니다.")
    @Min(value = 1, message = "학과 선택은 필수 입력 값입니다.")
    private Long departmentId;

    private UseYn useYn;

}
