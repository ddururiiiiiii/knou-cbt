package knou.cbt.domain.examquestion.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamQuestionDto {
    private Long id;
    private Long examId;
    private int questionNo;
    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answers;
    private String imageUrl;
}
