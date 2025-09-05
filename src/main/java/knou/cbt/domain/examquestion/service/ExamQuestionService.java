package knou.cbt.domain.examquestion.service;

import knou.cbt.domain.examquestion.dto.ExamQuestionRequest;
import knou.cbt.domain.examquestion.dto.ExamQuestionResponse;

import java.util.List;

public interface ExamQuestionService {
    List<ExamQuestionResponse> getQuestions(Long examId);
    void saveAll(Long examId, List<ExamQuestionRequest> requests);
}
