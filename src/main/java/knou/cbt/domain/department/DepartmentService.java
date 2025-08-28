package knou.cbt.domain.department;

import java.util.List;

public interface DepartmentService {

    List<Department> allDepartmentList(int page, int size);

    int countDepartments();
    List<Department> allSeachDepartmentList();

    void insert(Department department);

    void update(String departmentId, UpdateForm updateForm);

    String nameCheck(String departmentName);
}
