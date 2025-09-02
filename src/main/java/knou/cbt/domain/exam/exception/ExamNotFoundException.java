package knou.cbt.domain.exam.exception;

public class ExamNotFoundException extends RuntimeException {
    public ExamNotFoundException(Long id) {
        super("해당 시험을 찾을 수 없습니다. id=" + id);
    }
}
