package knou.cbt.domain.examquestion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import knou.cbt.domain.examquestion.model.ImageLayout;
import knou.cbt.domain.examquestion.model.OptionType;
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
    private MultipartFile imageFile;  // 문제 이미지 업로드용
    private String imageUrl; // DB에 들어가는 경로

    private OptionType optionType = OptionType.TEXT;
    private ImageLayout imageLayout;

    // 보기가 이미지인 경우 각 보기별 업로드 파일 (신규 업로드 시에만 값이 채워짐)
    private MultipartFile option1File;
    private MultipartFile option2File;
    private MultipartFile option3File;
    private MultipartFile option4File;
}
