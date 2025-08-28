package knou.cbt.domain.exam;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamSearchMapper {

    List<SearchResult> searchDepartmentId();

    List<SearchResult> searchSubjectId(@Param("searchValue")String searchValue);

    List<SearchResult> searchYear(@Param("searchValue")String searchValue);

    List<SearchResult> searchSemester(@Param("searchValue")String searchValue);

    List<SearchResult> searchCategory(@Param("searchValue")String searchValue);


}
