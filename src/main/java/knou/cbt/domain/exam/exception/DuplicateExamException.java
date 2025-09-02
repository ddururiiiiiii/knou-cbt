package knou.cbt.domain.exam.exception;

import knou.cbt.domain.exam.model.ExamType;

public class DuplicateExamException extends RuntimeException {
    public DuplicateExamException(String subjectName, String examType, int year) {
        super("이미 존재하는 시험입니다. 과목=" + subjectName + ", 구분=" + examType + ", 년도=" + year);
    }
}
