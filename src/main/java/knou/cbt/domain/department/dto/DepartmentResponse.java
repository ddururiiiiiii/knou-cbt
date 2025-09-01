package knou.cbt.domain.department.dto;

import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.department.model.Department;

public record DepartmentResponse(
        Long id,
        String departmentName,
        UseYn useYn
) {
    public static DepartmentResponse of(Department dept) {
        return new DepartmentResponse(
                dept.getId(),
                dept.getDepartmentName(),
                dept.getUseYn()
        );
    }
}
