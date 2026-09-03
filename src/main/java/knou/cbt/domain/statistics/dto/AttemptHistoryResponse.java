package knou.cbt.domain.statistics.dto;

import knou.cbt.domain.exam.model.ExamType;

import java.time.LocalDateTime;

public record AttemptHistoryResponse(
        Long examId,
        String subjectName,
        ExamType examType,
        int year,
        int score,
        int totalCount,
        Integer elapsedSeconds,
        LocalDateTime submittedAt
) {
}
