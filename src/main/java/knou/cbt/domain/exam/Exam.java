package knou.cbt.domain.exam;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class Exam {

    private String examId;
    private String subjectId;
    private int examYear;
    private int grade;
    private int semester;
    private int examCategory;
    private int questionId;
    private String useYn;

}
