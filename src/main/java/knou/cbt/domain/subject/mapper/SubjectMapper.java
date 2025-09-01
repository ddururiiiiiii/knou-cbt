package knou.cbt.domain.subject.mapper;

import knou.cbt.domain.subject.dto.SubjectResponse;
import knou.cbt.domain.subject.model.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubjectMapper {

    List<SubjectResponse> findAllWithDepartment(@Param("offset") int offset, @Param("limit") int limit,
                                                @Param("keyword") String keyword, @Param("useYn") String useYn);

    int countAll(@Param("keyword") String keyword,
                 @Param("useYn") String useYn);

    SubjectResponse findByIdWithDepartment(Long id);

    Subject findByNameWithDepartment(String name);

    void insert(Subject subject);

    void update(Subject subject);

    void delete(Long id);
}
