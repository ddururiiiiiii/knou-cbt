package knou.cbt.domain.statistics.dto;

import java.time.LocalDate;

public record DailyAttemptCountResponse(
        LocalDate date,
        long attemptCount
) {
}
