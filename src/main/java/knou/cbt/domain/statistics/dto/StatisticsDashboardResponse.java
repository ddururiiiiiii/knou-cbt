package knou.cbt.domain.statistics.dto;

import java.util.List;

public record StatisticsDashboardResponse(
        AttemptSummaryResponse summary,
        List<DailyAttemptCountResponse> dailyTrend, // 최근 14일
        List<SubjectRankingResponse> topSubjects,    // 응시 많은 과목 Top N
        List<ExamRankingResponse> topExams,          // 응시 많은 시험 Top N
        ContentCoverageResponse contentCoverage
) {
}
