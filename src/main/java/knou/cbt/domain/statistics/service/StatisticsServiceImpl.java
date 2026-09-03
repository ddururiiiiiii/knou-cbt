package knou.cbt.domain.statistics.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.exam.model.ExamType;
import knou.cbt.domain.statistics.dto.AttemptHistoryResponse;
import knou.cbt.domain.statistics.dto.AttemptSummaryResponse;
import knou.cbt.domain.statistics.dto.ContentCoverageResponse;
import knou.cbt.domain.statistics.dto.DailyAttemptCountResponse;
import knou.cbt.domain.statistics.dto.ExamRankingResponse;
import knou.cbt.domain.statistics.dto.StatisticsDashboardResponse;
import knou.cbt.domain.statistics.dto.SubjectRankingResponse;
import knou.cbt.domain.statistics.exception.AttemptNotFoundException;
import knou.cbt.domain.statistics.mapper.StatisticsMapper;
import knou.cbt.domain.statistics.model.ExamAttemptLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private static final int RECENT_TREND_DAYS = 14;
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

        List<SubjectRankingResponse> topSubjects = statisticsMapper.findTopSubjects(TOP_N);
        List<ExamRankingResponse> topExams = statisticsMapper.findTopExams(TOP_N);

        ContentCoverageResponse contentCoverage = new ContentCoverageResponse(
                statisticsMapper.countDepartments(),
                statisticsMapper.countSubjects(),
                statisticsMapper.countExams(),
                statisticsMapper.countExamQuestions(),
                statisticsMapper.findExamsWithoutQuestions()
        );

        return new StatisticsDashboardResponse(summary, dailyTrend, topSubjects, topExams, contentCoverage);
    }
}
