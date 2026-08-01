package knou.cbt.domain.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.exam.model.ExamType;
import knou.cbt.domain.subject.model.Semester;
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
        private String grade; // 콤마구분 다중 학년, 예: "1,3" (subject.grade와 동일)
        private Semester semester;
        private int year;
        private UseYn useYn;
        private UseYn subjectUseYn;
        private UseYn departmentUseYn;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;
}
