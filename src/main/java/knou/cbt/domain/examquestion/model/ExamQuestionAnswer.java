package knou.cbt.domain.examquestion.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExamQuestionAnswer {

    private Long id;
    private Long questionId;
    private String optionNo; // “2” or “2,3”

    public static ExamQuestionAnswer create(Long id, Long questionId, String answer) {
        return new ExamQuestionAnswer(id, questionId, answer);
    }
}
