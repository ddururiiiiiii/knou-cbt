package knou.cbt.domain.exam;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ExamInfo {

    private String examId;
    private String departmentName;
    private String subjectId;
    private String subjectName;
    private int subjectCategory;
    private int examYear;
    private int grade;
    private int semester;
    private int examCategory;
    private int questionId;
    private String useYn;

}
