package knou.cbt.domain.exam;


import java.util.List;

public interface ExamService {

    List<ExamInfo> allExamList(int page, int size, SearchCriteria searchCriteria);

    int countExams(int page, int size, SearchCriteria searchCriteria);

    List<ExamDetail> findByExamId(String examId);

    void saveExamInput(String examId, String studentId, String takenTime, String examDate, InputAnswer inputAnswer);

    void insertExam(Exam exam);

    void insertExamQuestions(ExamQuestions examQuestions);

    void updateExam(String examId, Exam exam);

    void updateExamQuestion(String examId, String questionId, ExamQuestions examQuestions);




}
