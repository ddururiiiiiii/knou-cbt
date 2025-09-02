package knou.cbt.domain.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.exam.model.ExamType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ExamDto {

        private Long id;
        private Long subjectId;
        private String subjectName;
        private Long departmentId;
        private String departmentName;
        private ExamType examType;
        private Integer grade;
        private int year;
        private UseYn useYn;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;
}
