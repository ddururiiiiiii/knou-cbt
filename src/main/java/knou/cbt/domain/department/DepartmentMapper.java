package knou.cbt.domain.department;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    List<Department> allDepartmentList(@Param("offset") int page, int size);

    List<Department> allSearchDepartmentList();

    int countDepartment();

    void insert(@Param("department") Department department);

    void update(@Param("departmentId")String departmentId, UpdateForm updateForm);
    Department findByDepartmentName(@Param("departmentName") String departmentName);




}
