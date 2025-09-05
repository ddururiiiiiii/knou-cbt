package knou.cbt.domain.examquestion.service;

import knou.cbt.domain.examquestion.dto.ExamQuestionRequest;
import knou.cbt.domain.examquestion.dto.ExamQuestionResponse;
import knou.cbt.domain.examquestion.mapper.ExamQuestionMapper;
import knou.cbt.domain.examquestion.model.ExamQuestion;
import knou.cbt.domain.examquestion.model.ExamQuestionAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private final ExamQuestionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ExamQuestionResponse> getQuestions(Long examId) {
        return mapper.findByExamId(examId).stream()
                .map(ExamQuestionResponse::of)
                .toList();
    }

    @Override
    @Transactional
    public void saveAll(Long examId, List<ExamQuestionRequest> requests) {
        // 기존 문제/정답 전체 삭제
        mapper.deleteByExamId(examId);

        // 신규 저장
        for (ExamQuestionRequest req : requests) {
            ExamQuestion question = ExamQuestion.create(
                    null, examId, req.getQuestionNo(),
                    req.getQuestionText(),
                    req.getOption1(), req.getOption2(),
                    req.getOption3(), req.getOption4()
            );
            mapper.insertQuestion(question);

            if (req.getAnswers() != null) {
                for (String answer : req.getAnswers().split(",")) {
                    ExamQuestionAnswer ans = ExamQuestionAnswer.create(null, question.getId(), answer.trim());
                    mapper.insertAnswer(ans);
                }
            }
        }
    }
}
