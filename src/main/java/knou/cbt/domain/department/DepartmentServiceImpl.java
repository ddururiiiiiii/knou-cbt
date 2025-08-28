package knou.cbt.domain.department;

import knou.cbt.domain.exam.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentMapper departmentMapper;


    @Override
    public List<Department> allDepartmentList(int page, int size) {
        return null;
    }

    @Override
    public int countDepartments() {
        return 0;
    }

    @Override
    public List<Department> allSeachDepartmentList() {
        return departmentMapper.allSearchDepartmentList();
    }

    @Override
    public void insert(Department department) {

    }

    @Override
    public void update(String departmentId, UpdateForm updateForm) {

    }

    @Override
    public String nameCheck(String departmentName) {
        return null;
    }
}
