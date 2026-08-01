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
                    blankToNull(req.getOption1()), blankToNull(req.getOption2()),
                    blankToNull(req.getOption3()), blankToNull(req.getOption4()),
                    blankToNull(req.getImageUrl()),
                    req.getOptionType(), req.getImageLayout()
            );
            mapper.insertQuestion(question);

            if (req.getAnswers() != null && !req.getAnswers().isBlank()) {
                for (String answer : req.getAnswers().split(",")) {
                    String trimmed = answer.trim();
                    if (trimmed.isEmpty()) {
                        continue; // "1,,3"처럼 빈 항목이 섞여도 무시
                    }
                    Integer optionNo = Integer.parseInt(trimmed); // 문자열 → 숫자 변환
                    ExamQuestionAnswer ans = ExamQuestionAnswer.create(null, question.getId(), optionNo);
                    mapper.insertAnswer(ans);
                }
            }
        }
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
