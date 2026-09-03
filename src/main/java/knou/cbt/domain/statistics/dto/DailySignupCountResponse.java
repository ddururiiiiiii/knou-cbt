package knou.cbt.domain.statistics.dto;

import java.time.LocalDate;

public record DailySignupCountResponse(LocalDate date, long signupCount) {
}
