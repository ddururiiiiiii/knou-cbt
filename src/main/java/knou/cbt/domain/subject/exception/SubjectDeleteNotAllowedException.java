package knou.cbt.domain.subject.exception;

public class SubjectDeleteNotAllowedException extends RuntimeException {
    public SubjectDeleteNotAllowedException(Long subjectId) {
        super("해당 과목(ID=" + subjectId + ")에 시험이 존재하여 삭제할 수 없습니다.");
    }
}
