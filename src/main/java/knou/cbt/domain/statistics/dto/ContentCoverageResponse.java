package knou.cbt.domain.statistics.dto;

import java.util.List;

public record ContentCoverageResponse(
        long departmentCount,
        long subjectCount,
        long examCount,
        long examQuestionCount,
        List<String> examsWithoutQuestions // "2024년도 컴퓨터과학 기말시험" 형태의 표시용 문자열
) {
}
