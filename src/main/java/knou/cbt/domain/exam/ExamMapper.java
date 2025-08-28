package knou.cbt.domain.exam;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamMapper {

    List<ExamInfo> allExamList(@Param("offset") int offset, @Param("limit") int limit, SearchCriteria searchCriteria);

    int countExams(@Param("offset") int offset, @Param("limit") int limit, @Param("searchCriteria") SearchCriteria searchCriteria);

    List<ExamDetail> findByExamId(@Param("examId") String examId);

    void saveExamInput(@Param("examId") String examId, @Param("studentId") String studentId
                     , @Param("takenTime") String takenTime, @Param("examDate") String examDate
                    , @Param("inputAnswer") InputAnswer inputAnswer);

    void insertExam(@Param("exam") Exam exam);

    void insertExamQuestions(@Param("exam") ExamQuestions examQuestions);

    void updateExamQuestion(@Param("examId") String examId, @Param("questionId") String questionId, ExamQuestions examQuestions);



}
