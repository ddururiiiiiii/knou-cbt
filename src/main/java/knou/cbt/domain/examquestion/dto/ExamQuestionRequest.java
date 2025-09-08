package knou.cbt.domain.examquestion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ExamQuestionRequest {

    @NotNull(message = "문제 번호는 필수입니다.")
    private Long id;
    private Long examId;
    private int questionNo;
    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    @Pattern(regexp = "^\\d+(,\\d+)*$", message = "정답은 숫자(쉼표로 구분)만 입력 가능합니다.")
    private String answers; // “2” or “2,3”
    private MultipartFile imageFile;  // 파일 업로드용
    private String imageUrl; // DB에 들어가는 경로
}
