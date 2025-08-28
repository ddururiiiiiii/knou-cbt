package knou.cbt.domain.exam;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService{

    private final ExamMapper examMapper;

    @Override
    public List<ExamInfo> allExamList(int page, int size, SearchCriteria searchCriteria) {
        int offset = (page - 1) * size;
        return examMapper.allExamList(offset, size, searchCriteria);
    }


    @Override
    public int countExams(int page, int size, SearchCriteria searchCriteria) {
        int offset = (page - 1) * size;
        return examMapper.countExams(offset, size, searchCriteria);
    }

    @Override
    public List<ExamDetail> findByExamId(String examId) {
        return examMapper.findByExamId(examId);
    }

    @Override
    public void saveExamInput(String examId, String studentId, String takenTime, String examDate, InputAnswer inputAnswer) {
        examMapper.saveExamInput(examId, studentId, takenTime, examDate, inputAnswer);
    }

    @Override
    public void insertExam(Exam exam) {
        examMapper.insertExam(exam);
    }

    @Override
    public void insertExamQuestions(ExamQuestions examQuestions) {
        examMapper.insertExamQuestions(examQuestions);
    }

    @Override
    public void updateExam(String examId, Exam exam) {

    }

    @Override
    public void updateExamQuestion(String examId, String questionId, ExamQuestions examQuestions) {

    }



}
