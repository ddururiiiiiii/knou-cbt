package knou.cbt.domain.department.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.department.dto.DepartmentCreateRequest;
import knou.cbt.domain.department.dto.DepartmentResponse;
import knou.cbt.domain.department.dto.DepartmentUpdateRequest;
import knou.cbt.domain.department.model.Department;

import java.util.List;

public interface DepartmentService {

    PageResponse<DepartmentResponse> listPage(String keyword, String useYn, PageRequest pageRequest);
    int count(String keyword, String useYn);
    DepartmentResponse get(Long id);
    void create(DepartmentCreateRequest req);
    void update(Long id, DepartmentUpdateRequest req);
    void delete(Long id);
}
