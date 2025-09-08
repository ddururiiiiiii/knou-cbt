package knou.cbt.domain.department.exception;

public class DepartmentDeleteNotAllowedException extends RuntimeException {
    public DepartmentDeleteNotAllowedException(Long deptId) {
        super("해당 학과(ID=" + deptId + ")에 과목이 존재하여 삭제할 수 없습니다.");
    }
}