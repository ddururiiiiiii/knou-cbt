package knou.cbt.domain.examquestion.mapper;

import knou.cbt.domain.examquestion.dto.ExamQuestionDto;
import knou.cbt.domain.examquestion.model.ExamQuestion;
import knou.cbt.domain.examquestion.model.ExamQuestionAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamQuestionMapper {
    List<ExamQuestionDto> findByExamId(@Param("examId") Long examId);

    void deleteByExamId(@Param("examId") Long examId);

    void insertQuestion(ExamQuestion question);

    void insertAnswer(ExamQuestionAnswer answer);
}
