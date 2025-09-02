package knou.cbt.domain.department.dto.mapper;

import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.department.dto.DepartmentCreateRequest;
import knou.cbt.domain.department.dto.DepartmentResponse;
import knou.cbt.domain.department.dto.DepartmentUpdateRequest;
import knou.cbt.domain.department.model.Department;

public class DepartmentDtoMapper {

    private DepartmentDtoMapper() {
        // 유틸 클래스이므로 인스턴스화 방지
    }

    // DTO -> Entity
    public static Department fromCreateRequest(DepartmentCreateRequest request) {
        return Department.create(null, request.getDepartmentName(), UseYn.Y, null, null);
    }

    public static Department fromUpdateRequest(Long id, DepartmentUpdateRequest req) {
        return Department.create(id, req.getDepartmentName(), req.getUseYn(), null, null);
    }

    public static DepartmentUpdateRequest toUpdateRequest(DepartmentResponse resp) {
        return DepartmentUpdateRequest.builder()
                .departmentName(resp.departmentName())
                .useYn(resp.useYn())
                .build();
    }

}
