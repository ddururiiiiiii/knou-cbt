package knou.cbt.domain.statistics.mapper;

import knou.cbt.domain.statistics.dto.DailyAttemptCountResponse;
import knou.cbt.domain.statistics.dto.ExamRankingResponse;
import knou.cbt.domain.statistics.dto.SubjectRankingResponse;
import knou.cbt.domain.statistics.model.ExamAttemptLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StatisticsMapper {

    void insertAttemptLog(ExamAttemptLog log);

    long countAttemptsSince(@Param("since") LocalDate since);

    long countAttemptsTotal();

    List<DailyAttemptCountResponse> findDailyAttemptCounts(@Param("since") LocalDate since);

    List<SubjectRankingResponse> findTopSubjects(@Param("limit") int limit);

    List<ExamRankingResponse> findTopExams(@Param("limit") int limit);

    long countDepartments();

    long countSubjects();

    long countExams();

    long countExamQuestions();

    List<String> findExamsWithoutQuestions();
}
