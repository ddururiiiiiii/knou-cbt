package knou.cbt.domain.statistics.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.exam.model.ExamType;
import knou.cbt.domain.statistics.dto.AttemptHistoryResponse;
import knou.cbt.domain.statistics.dto.StatisticsDashboardResponse;

public interface StatisticsService {

    void logAttempt(Long examId,
                     Long subjectId,
                     String subjectName,
                     ExamType examType,
                     int year,
                     int score,
                     int totalCount,
                     Integer elapsedSeconds,
                     Long userId);

    StatisticsDashboardResponse getDashboard();

    PageResponse<AttemptHistoryResponse> getMemberAttemptHistory(Long userId, PageRequest pageRequest);

    void anonymizeMemberAttempts(Long userId);
}
