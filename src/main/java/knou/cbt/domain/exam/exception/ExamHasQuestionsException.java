package knou.cbt.domain.exam.exception;

public class ExamHasQuestionsException extends RuntimeException {
    public ExamHasQuestionsException(Long examId) {
        super("문제가 존재하는 시험은 삭제할 수 없습니다. examId=" + examId);
    }
}
