package knou.cbt.domain.examquestion.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExamQuestion {
    private Long id;
    private Long examId;
    private int questionNo;
    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String imageUrl;

    public static ExamQuestion create(Long id, Long examId, int questionNo,
                                      String questionText,
                                      String option1, String option2,
                                      String option3, String option4, String imageUrl) {
        return new ExamQuestion(id, examId, questionNo, questionText,
                option1, option2, option3, option4, imageUrl);
    }
}
