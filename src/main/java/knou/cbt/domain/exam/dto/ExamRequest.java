package knou.cbt.domain.exam.dto;

import jakarta.validation.constraints.NotNull;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.exam.model.ExamType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamRequest {

    // 수정 시에만 사용됨
    private Long id;

    @NotNull(message = "과목 선택은 필수 입력 값입니다.")
    private Long subjectId;

    @NotNull(message = "시험 구분은 필수 입력 값입니다.")
    private ExamType examType;

    @NotNull(message = "년도 선택은 필수 입력 값입니다.")
    private Integer year;

    private UseYn useYn;
}
