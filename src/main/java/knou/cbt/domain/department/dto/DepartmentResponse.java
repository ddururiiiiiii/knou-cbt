package knou.cbt.domain.department.dto;

import knou.cbt.domain.department.model.Department;

public record DepartmentResponse(
        Long id,
        String departmentName,
        String useYn,
        String displayStatus
) {
    public static DepartmentResponse of(Department dept) {
        String status = "Y".equals(dept.getUseYn()) ? "사용" : "미사용";
        return new DepartmentResponse(
                dept.getId(),
                dept.getDepartmentName(),
                dept.getUseYn(),
                status
        );
    }
}
