package knou.cbt.domain.department.exception;

public class DuplicateDepartmentNameException extends RuntimeException {
    public DuplicateDepartmentNameException(String name) {
        super("이미 존재하는 학과명입니다 : " + name);
    }
}