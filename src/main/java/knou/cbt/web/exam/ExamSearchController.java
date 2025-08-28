package knou.cbt.web.exam;

import knou.cbt.domain.exam.ExamSearchService;
import knou.cbt.domain.exam.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/search")
public class ExamSearchController {

    private final ExamSearchService examSearchService;

    @GetMapping("/api")
    public ResponseEntity<List<SearchResult>> searchApiList(
      @RequestParam("searchKey") String searchKey
    , @RequestParam(value = "searchValue", required = false) String searchValue) {

        List<SearchResult> resultList = switch (searchKey) {
            case "departmentId" -> examSearchService.searchDepartmentId();
            case "subjectId" -> examSearchService.searchSubjectId(searchValue);
            case "year" -> examSearchService.searchYear(searchValue);
            case "semester" -> examSearchService.searchSemester(searchValue);
            default -> examSearchService.searchCategory(searchValue);
        };

        return ResponseEntity.ok().body(resultList);
    }

}
