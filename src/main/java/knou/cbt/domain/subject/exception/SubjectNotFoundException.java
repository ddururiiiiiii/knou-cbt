package knou.cbt.domain.subject.exception;

public class SubjectNotFoundException extends RuntimeException {
    public SubjectNotFoundException(Long id) {
        super("해당 과목(ID: " + id + ")은 존재하지 않습니다.");
    }
}