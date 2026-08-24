package knou.cbt.domain.statistics.dto;

public record AttemptSummaryResponse(
        long todayCount,
        long last7DaysCount,
        long totalCount
) {
}
