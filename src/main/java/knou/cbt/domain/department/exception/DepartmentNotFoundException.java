package knou.cbt.domain.department.exception;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(Long id) {
        super("해당 학과(ID: " + id + ")는 존재하지 않습니다.");
    }
}