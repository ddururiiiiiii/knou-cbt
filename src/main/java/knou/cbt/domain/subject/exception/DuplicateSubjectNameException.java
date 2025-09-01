package knou.cbt.domain.subject.exception;

public class DuplicateSubjectNameException extends RuntimeException {
    public DuplicateSubjectNameException(String name) {
        super("이미 존재하는 과목명입니다 : " + name);
    }
}