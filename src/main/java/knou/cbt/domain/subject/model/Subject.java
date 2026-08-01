package knou.cbt.domain.subject.model;


import knou.cbt.domain.common.model.UseYn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Subject {

    private Long id;
    private String subjectName;
    private SubjectCategory subjectCategory;
    private Long departmentId;
    private String grade; // 콤마구분 다중 학년, 예: "1,3" (GradeCsv 참고)
    private Semester semester;
    private UseYn useYn;

    //정적 팩토리 메서드
    public static Subject create(Long id,
                                 String subjectName,
                                 SubjectCategory subjectCategory,
                                 Long departmentId,
                                 String grade,
                                 Semester semester,
                                 UseYn useYn) {
        return new Subject(id,
                           subjectName,
                           subjectCategory,
                           departmentId,
                           grade,
                           semester,
                           useYn);
    }
}
