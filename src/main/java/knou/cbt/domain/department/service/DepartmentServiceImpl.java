package knou.cbt.domain.department.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.department.dto.DepartmentCreateRequest;
import knou.cbt.domain.department.dto.DepartmentResponse;
import knou.cbt.domain.department.dto.DepartmentUpdateRequest;
import knou.cbt.domain.department.dto.mapper.DepartmentDtoMapper;
import knou.cbt.domain.department.exception.DepartmentNotFoundException;
import knou.cbt.domain.department.exception.DuplicateDepartmentNameException;
import knou.cbt.domain.department.mapper.DepartmentMapper;
import knou.cbt.domain.department.model.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> listPage(String keyword, String useYn, PageRequest pageRequest) {
        List<DepartmentResponse> content = mapper.findAll(
                        pageRequest.offset(),
                        pageRequest.sizeOrDefault(),
                        keyword,
                        useYn
                ).stream()
                .map(DepartmentResponse::of)
                .toList();

        int total = mapper.countAll(keyword, useYn);
        int totalPages = (int) Math.ceil((double) total / pageRequest.sizeOrDefault());

        return new PageResponse<>(content,
                pageRequest.pageOrDefault(),
                pageRequest.sizeOrDefault(),
                total,
                totalPages);
    }

    @Override
    @Transactional(readOnly = true)
    public int count(String keyword, String useYn) {
        return mapper.countAll(keyword, useYn);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse get(Long id) {
        Department dept = findDepartmentOrThrow(id);
        return DepartmentResponse.of(dept);
    }

    @Override
    public void create(DepartmentCreateRequest req) {
        validateDuplicateName(req.getDepartmentName(), null);

        Department department = DepartmentDtoMapper.fromCreateRequest(req);
        mapper.insert(department);
    }

    @Override
    public void update(Long id, DepartmentUpdateRequest req) {
        validateDuplicateName(req.getDepartmentName(), id);
        findDepartmentOrThrow(id);
        Department department = DepartmentDtoMapper.fromUpdateRequest(id, req);
        mapper.update(department);
    }

    @Override
    public void delete(Long id) {
        findDepartmentOrThrow(id);
        mapper.delete(id);
    }


    private void validateDuplicateName(String name, Long excludeId) {
        Department existing = mapper.findByName(name);
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw new DuplicateDepartmentNameException(name);
        }
    }

    private Department findDepartmentOrThrow(Long id) {
        Department dept = mapper.findById(id);
        if (dept == null) {
            throw new DepartmentNotFoundException(id);
        }
        return dept;
    }
}
