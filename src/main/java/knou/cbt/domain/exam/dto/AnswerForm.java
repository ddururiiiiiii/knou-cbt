package knou.cbt.domain.exam.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnswerForm {
    private List<String> answers;
}
