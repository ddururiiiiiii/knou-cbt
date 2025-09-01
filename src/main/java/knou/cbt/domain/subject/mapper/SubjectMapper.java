package knou.cbt.domain.subject.mapper;

import knou.cbt.domain.subject.dto.SubjectDto;
import knou.cbt.domain.subject.model.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubjectMapper {

    List<SubjectDto> findAllWithDepartment(@Param("offset") int offset, @Param("limit") int limit,
                                           @Param("keyword") String keyword, @Param("useYn") String useYn);

    int countAll(@Param("keyword") String keyword,
                 @Param("useYn") String useYn);

    SubjectDto findByIdWithDepartment(Long id);

    SubjectDto findByNameWithDepartment(String name);

    void insert(Subject subject);

    void update(Subject subject);

    void delete(Long id);
}
