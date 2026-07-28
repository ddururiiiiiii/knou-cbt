package knou.cbt.domain.examquestion.dto;

import knou.cbt.domain.examquestion.model.ImageLayout;
import knou.cbt.domain.examquestion.model.OptionType;

public record ExamQuestionResponse(
        Long id,
        Long examId,
        int questionNo,
        String questionText,
        String option1,
        String option2,
        String option3,
        String option4,
        String answers,
        String imageUrl,
        OptionType optionType,
        ImageLayout imageLayout

) {
    public static ExamQuestionResponse of(ExamQuestionDto dto) {
        return new ExamQuestionResponse(
                dto.getId(),
                dto.getExamId(),
                dto.getQuestionNo(),
                dto.getQuestionText(),
                dto.getOption1(),
                dto.getOption2(),
                dto.getOption3(),
                dto.getOption4(),
                dto.getAnswers(),
                dto.getImageUrl(),
                dto.getOptionType(),
                dto.getImageLayout()
        );
    }
}
