package knou.cbt.domain.statistics.dto;

public record SubjectRankingResponse(
        Long subjectId,
        String subjectName,
        long attemptCount,
        double averageAccuracy // 0.0 ~ 1.0
) {
}
