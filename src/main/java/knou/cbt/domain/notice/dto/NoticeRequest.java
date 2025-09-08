package knou.cbt.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import knou.cbt.domain.common.model.UseYn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeRequest {

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    private Boolean pinned;

    private UseYn useYn;
}
