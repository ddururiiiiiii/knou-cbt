package knou.cbt.domain.statistics.dto;

import java.util.List;

public record MemberStatsResponse(
        long totalMembers,
        long todaySignups,
        long last7DaysSignups,
        List<DailySignupCountResponse> signupTrend,
        List<ProviderCountResponse> providerBreakdown,
        long memberAttemptCount,
        long anonymousAttemptCount
) {
    public double memberAttemptRate() {
        long totalAttempts = memberAttemptCount + anonymousAttemptCount;
        return totalAttempts == 0 ? 0 : (double) memberAttemptCount * 100 / totalAttempts;
    }

    public double providerRate(ProviderCountResponse provider) {
        return totalMembers == 0 ? 0 : (double) provider.count() * 100 / totalMembers;
    }
}
