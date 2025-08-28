package knou.cbt.domain.exam;

import java.util.List;

public interface ExamSearchService {

    List<SearchResult> searchDepartmentId();

    List<SearchResult> searchSubjectId(String searchValue);

    List<SearchResult> searchYear(String searchValue);

    List<SearchResult> searchSemester(String searchValue);

    List<SearchResult> searchCategory(String searchValue);

}
