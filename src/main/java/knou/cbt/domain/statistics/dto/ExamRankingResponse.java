package knou.cbt.domain.statistics.dto;

import knou.cbt.domain.exam.model.ExamType;

public record ExamRankingResponse(
        Long examId,
        String subjectName,
        ExamType examType,
        int year,
        long attemptCount
) {
    public String displayName() {
        return year + "년도 " + subjectName + " " + examType.getDescription();
    }
}
