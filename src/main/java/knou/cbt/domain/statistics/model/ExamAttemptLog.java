package knou.cbt.domain.statistics.model;

import knou.cbt.domain.exam.model.ExamType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExamAttemptLog {

    private Long id;
    private Long examId;
    private Long subjectId;
    private String subjectName;
    private ExamType examType;
    private int year;
    private int score;
    private int totalCount;
    private Integer elapsedSeconds;
    private Long userId;

    public static ExamAttemptLog of(Long examId,
                                     Long subjectId,
                                     String subjectName,
                                     ExamType examType,
                                     int year,
                                     int score,
                                     int totalCount,
                                     Integer elapsedSeconds,
                                     Long userId) {
        return new ExamAttemptLog(null, examId, subjectId, subjectName, examType, year,
                score, totalCount, elapsedSeconds, userId);
    }
}
