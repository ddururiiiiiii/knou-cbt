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
    private Integer grade;
    private UseYn useYn;

    //정적 팩토리 메서드
    public static Subject create(Long id,
                                 String subjectName,
                                 SubjectCategory subjectCategory,
                                 Long departmentId,
                                 Integer grade,
                                 UseYn useYn) {
        return new Subject(id,
                           subjectName,
                           subjectCategory,
                           departmentId,
                           grade,
                           useYn);
    }
}
