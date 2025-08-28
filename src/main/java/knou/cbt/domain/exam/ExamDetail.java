package knou.cbt.domain.exam;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter @RequiredArgsConstructor
public class ExamDetail {

    private String examId;
    private String departmentName;
    private String subjectId;
    private String subjectName;
    private int examYear;
    private int grade;
    private int semester;
    private int examCategory;
    private int questionNo;
    private String question;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private int answer;
    private String useYn;

}
