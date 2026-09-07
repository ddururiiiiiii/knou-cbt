package knou.cbt.domain.statistics.dto;

public record MonthlyAttemptCountResponse(
        String yearMonth, // "YYYY-MM"
        long attemptCount
) {
}
