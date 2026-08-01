package knou.cbt.domain.subject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.subject.model.Semester;
import knou.cbt.domain.subject.model.SubjectCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubjectRequest {

    private Long id;

    @NotBlank(message = "과목명은 필수 입력 값입니다.")
    @Size(max = 50, message = "과목명은 최대 50자까지 가능합니다.")
    private String subjectName;

    @NotNull(message = "과목 구분은 필수 입력 값입니다.")
    private SubjectCategory subjectCategory;

    @NotEmpty(message = "학년은 최소 1개 이상 선택해야 합니다.")
    private List<Integer> grades;

    @NotNull(message = "학기 선택은 필수 입력 값입니다.")
    private Semester semester;

    @NotNull(message = "학과 선택은 필수 입력 값입니다.")
    @Min(value = 1, message = "학과 선택은 필수 입력 값입니다.")
    private Long departmentId;

    private UseYn useYn;

}
