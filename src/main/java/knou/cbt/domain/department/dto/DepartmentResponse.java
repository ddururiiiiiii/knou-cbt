package knou.cbt.domain.department.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.department.model.Department;

import java.time.LocalDateTime;

public record DepartmentResponse(
        Long id,
        String departmentName,
        UseYn useYn,
        boolean hasSubjects,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
    public static DepartmentResponse of(Department dept, boolean hasSubjects) {
        return new DepartmentResponse(
                dept.getId(),
                dept.getDepartmentName(),
                dept.getUseYn(),
                hasSubjects,
                dept.getCreatedAt(),
                dept.getUpdatedAt()
        );
    }

    public static DepartmentResponse of(Department dept) {
        return of(dept, false);
    }
}
