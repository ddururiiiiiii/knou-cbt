package knou.cbt.domain.examquestion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExamQuestionRequestList {
    private List<ExamQuestionRequest> questions;
}
