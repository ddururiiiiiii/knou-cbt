package knou.cbt.domain.statistics.service;

import knou.cbt.domain.exam.model.ExamType;
import knou.cbt.domain.statistics.dto.StatisticsDashboardResponse;

public interface StatisticsService {

    void logAttempt(Long examId,
                     Long subjectId,
                     String subjectName,
                     ExamType examType,
                     int year,
                     int score,
                     int totalCount,
                     Integer elapsedSeconds);

    StatisticsDashboardResponse getDashboard();
}
