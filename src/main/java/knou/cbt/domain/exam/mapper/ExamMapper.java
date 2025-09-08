package knou.cbt.domain.exam.mapper;

import knou.cbt.domain.exam.dto.ExamDto;
import knou.cbt.domain.exam.model.Exam;
import knou.cbt.domain.exam.model.ExamType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamMapper {


    List<ExamDto> findAllExamDetails(@Param("offset") int offset,
                                     @Param("limit") int limit,
                                     @Param("useYn") String useYn,
                                     @Param("departmentId") Long departmentId,
                                     @Param("subjectId") Long subjectId,
                                     @Param("examType") ExamType examType,
                                     @Param("year") Integer year);

    int countAll(@Param("useYn") String useYn,
                 @Param("departmentId") Long departmentId,
                 @Param("subjectId") Long subjectId,
                 @Param("examType") ExamType examType,
                 @Param("year") Integer year);

    ExamDto findExamExtendedById(@Param("id") Long id);

    ExamDto findDuplicate(@Param("subjectId") Long subjectId,
                          @Param("examType") ExamType examType,
                          @Param("year") int year
                          );
    void insert(Exam exam);

    void update(Exam exam);

    void delete(Long id);

    boolean existsBySubjectId(@Param("subjectId") Long subjectId);
}