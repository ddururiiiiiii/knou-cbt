package knou.cbt.domain.exam.model;

import knou.cbt.domain.common.model.UseYn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Exam {

    private Long id;
    private Long subjectId;
    private ExamType examType;
    private int year;
    private UseYn useYn;

    // 정적 팩토리 메서드
    public static Exam create(Long id,
                              Long subjectId,
                              ExamType examType,
                              int year,
                              UseYn useYn) {
        return new Exam(id,
                subjectId,
                examType,
                year,
                useYn);
    }
}

