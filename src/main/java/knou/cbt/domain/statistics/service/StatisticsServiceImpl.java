package knou.cbt.domain.statistics.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.exam.model.ExamType;
import knou.cbt.domain.statistics.dto.AttemptHistoryResponse;
import knou.cbt.domain.statistics.dto.AttemptSummaryResponse;
import knou.cbt.domain.statistics.dto.ContentCoverageResponse;
import knou.cbt.domain.statistics.dto.DailyAttemptCountResponse;
import knou.cbt.domain.statistics.dto.ExamRankingResponse;
import knou.cbt.domain.statistics.dto.MemberStatsResponse;
import knou.cbt.domain.statistics.dto.MonthlyAttemptCountResponse;
import knou.cbt.domain.statistics.dto.StatisticsDashboardResponse;
import knou.cbt.domain.statistics.dto.SubjectRankingResponse;
import knou.cbt.domain.statistics.exception.AttemptNotFoundException;
import knou.cbt.domain.statistics.mapper.StatisticsMapper;
import knou.cbt.domain.statistics.model.ExamAttemptLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private static final int RECENT_TREND_DAYS = 14;
    private static final int RECENT_TREND_MONTHS = 12;
    private static final int TOP_N = 10;

    private final StatisticsMapper statisticsMapper;

    @Override
    public void logAttempt(Long examId,
                            Long subjectId,
                            String subjectName,
                            ExamType examType,
                            int year,
                            int score,
                            int totalCount,
                            Integer elapsedSeconds,
                            Long userId,
                            String answers) {
        statisticsMapper.insertAttemptLog(
                ExamAttemptLog.of(examId, subjectId, subjectName, examType, year, score, totalCount, elapsedSeconds, userId, answers));
    }

    @Override
    public PageResponse<AttemptHistoryResponse> getMemberAttemptHistory(Long userId, PageRequest pageRequest) {
        long totalElements = statisticsMapper.countAttemptsByUserId(userId);
        List<AttemptHistoryResponse> content = statisticsMapper.findAttemptsByUserId(
                userId, pageRequest.sizeOrDefault(), pageRequest.offset());
        int totalPages = (int) Math.ceil((double) totalElements / pageRequest.sizeOrDefault());

        return new PageResponse<>(content, pageRequest.pageOrDefault(), pageRequest.sizeOrDefault(),
                totalElements, totalPages);
    }

    @Override
    public AttemptHistoryResponse getMemberAttemptDetail(Long attemptId, Long userId) {
        AttemptHistoryResponse attempt = statisticsMapper.findMemberAttemptDetail(attemptId, userId);
        if (attempt == null) {
            throw new AttemptNotFoundException(attemptId);
        }
        return attempt;
    }

    @Override
    public void anonymizeMemberAttempts(Long userId) {
        statisticsMapper.anonymizeAttemptLogsByUserId(userId);
    }

    @Override
    public StatisticsDashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();

        AttemptSummaryResponse summary = new AttemptSummaryResponse(
                statisticsMapper.countAttemptsSince(today),
                statisticsMapper.countAttemptsSince(today.minusDays(6)),
                statisticsMapper.countAttemptsTotal()
        );

        List<DailyAttemptCountResponse> dailyTrend =
                statisticsMapper.findDailyAttemptCounts(today.minusDays(RECENT_TREND_DAYS - 1L));

        List<MonthlyAttemptCountResponse> monthlyTrend = fillMissingMonths(
                statisticsMapper.findMonthlyAttemptCounts(today.withDayOfMonth(1).minusMonths(RECENT_TREND_MONTHS - 1L)),
                YearMonth.from(today));

        List<SubjectRankingResponse> topSubjects = statisticsMapper.findTopSubjects(TOP_N);
        List<ExamRankingResponse> topExams = statisticsMapper.findTopExams(TOP_N);

        ContentCoverageResponse contentCoverage = new ContentCoverageResponse(
                statisticsMapper.countDepartments(),
                statisticsMapper.countSubjects(),
                statisticsMapper.countExams(),
                statisticsMapper.countExamQuestions(),
                statisticsMapper.findExamsWithoutQuestions()
        );

        return new StatisticsDashboardResponse(summary, dailyTrend, monthlyTrend, topSubjects, topExams, contentCoverage);
    }

    // 데이터 없는 달은 집계 쿼리 결과에서 빠지므로, 월별 비교 차트에서 공백 없이 12개월이
    // 전부 보이도록 0건으로 채워준다.
    private List<MonthlyAttemptCountResponse> fillMissingMonths(List<MonthlyAttemptCountResponse> counts,
                                                                  YearMonth currentMonth) {
        Map<String, Long> countsByMonth = counts.stream()
                .collect(Collectors.toMap(MonthlyAttemptCountResponse::yearMonth, MonthlyAttemptCountResponse::attemptCount));

        DateTimeFormatter yearMonthFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        return IntStream.range(0, RECENT_TREND_MONTHS)
                .mapToObj(i -> currentMonth.minusMonths(RECENT_TREND_MONTHS - 1L - i))
                .map(ym -> {
                    String key = ym.format(yearMonthFormat);
                    return new MonthlyAttemptCountResponse(key, countsByMonth.getOrDefault(key, 0L));
                })
                .toList();
    }

    @Override
    public MemberStatsResponse getMemberStats() {
        LocalDate today = LocalDate.now();

        long totalMembers = statisticsMapper.countMembersTotal();
        long todaySignups = statisticsMapper.countMembersSince(today);
        long last7DaysSignups = statisticsMapper.countMembersSince(today.minusDays(6));

        var signupTrend = statisticsMapper.findDailySignupCounts(today.minusDays(RECENT_TREND_DAYS - 1L));
        var providerBreakdown = statisticsMapper.countMembersByProvider();

        long memberAttemptCount = statisticsMapper.countAttemptsWithMember();
        long anonymousAttemptCount = statisticsMapper.countAttemptsWithoutMember();

        return new MemberStatsResponse(totalMembers, todaySignups, last7DaysSignups,
                signupTrend, providerBreakdown, memberAttemptCount, anonymousAttemptCount);
    }
}
