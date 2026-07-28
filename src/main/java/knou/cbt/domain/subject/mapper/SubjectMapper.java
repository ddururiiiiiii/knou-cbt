package knou.cbt.domain.subject.mapper;

import knou.cbt.domain.subject.dto.SubjectDto;
import knou.cbt.domain.subject.model.Subject;
import knou.cbt.domain.subject.model.SubjectCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface SubjectMapper {

    List<SubjectDto> findAll();

    List<SubjectDto> findAllWithDepartment(@Param("offset") int offset, @Param("limit") int limit,
                                           @Param("keyword") String keyword, @Param("useYn") String useYn,
                                           @Param("departmentId") Long departmentId,
                                           @Param("grade") Integer grade,
                                           @Param("subjectCategory") SubjectCategory subjectCategory);

    int countAll(@Param("keyword") String keyword,
                 @Param("useYn") String useYn,
                 @Param("departmentId") Long departmentId,
                 @Param("grade") Integer grade,
                 @Param("subjectCategory") SubjectCategory subjectCategory);

    SubjectDto findByIdWithDepartment(Long id);

    SubjectDto findByNameWithDepartment(String name);

    List<SubjectDto> findByDepartmentId(@Param("deptId") Long deptId);

    void insert(Subject subject);

    void update(Subject subject);

    void delete(Long id);

    boolean existsByDepartmentId(@Param("deptId") Long deptId);

    Set<Long> findDepartmentIdsWithSubjects(@Param("deptIds") List<Long> deptIds);

    List<Integer> findGradesByDepartment(@Param("departmentId") Long departmentId);

    List<SubjectCategory> findCategoriesByDepartmentAndGrade(@Param("departmentId") Long departmentId,
                                                              @Param("grade") Integer grade);

    List<SubjectDto> findTopSubjectsByExamCount(@Param("limit") int limit);
}
