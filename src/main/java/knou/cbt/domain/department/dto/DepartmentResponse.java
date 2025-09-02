package knou.cbt.domain.department.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.department.model.Department;

import java.time.LocalDateTime;

public record DepartmentResponse(
        Long id,
        String departmentName,
        UseYn useYn,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
    public static DepartmentResponse of(Department dept) {
        return new DepartmentResponse(
                dept.getId(),
                dept.getDepartmentName(),
                dept.getUseYn(),
                dept.getCreatedAt(),
                dept.getUpdatedAt()
        );
    }
}
