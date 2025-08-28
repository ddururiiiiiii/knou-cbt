package knou.cbt.domain.exam;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamSearchServiceImpl implements ExamSearchService {

    private final ExamSearchMapper examSearchMapper;

    @Override
    public List<SearchResult> searchDepartmentId() {
        return examSearchMapper.searchDepartmentId();
    }

    @Override
    public List<SearchResult> searchSubjectId(String searchValue) {
        return examSearchMapper.searchSubjectId(searchValue);
    }

    @Override
    public List<SearchResult> searchYear(String searchValue) {
        return examSearchMapper.searchYear(searchValue);
    }

    @Override
    public List<SearchResult> searchSemester(String searchValue) {
        return examSearchMapper.searchSemester(searchValue);
    }

    @Override
    public List<SearchResult> searchCategory(String searchValue) {
        return examSearchMapper.searchCategory(searchValue);
    }
}
