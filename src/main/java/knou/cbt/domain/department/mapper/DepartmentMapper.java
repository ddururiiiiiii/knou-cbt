package knou.cbt.domain.department.mapper;

import knou.cbt.domain.department.model.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepartmentMapper {


    List<Department> findAll(@Param("offset") int offset, @Param("limit") int limit,
                             @Param("keyword") String keyword, @Param("useYn") String useYn);

    int countAll(@Param("keyword") String keyword,
                 @Param("useYn") String useYn);

    Department findById(Long id);

    Department findByName(String name);

    void insert(Department department);

    void update(Department department);

    void delete(Long id);




}
