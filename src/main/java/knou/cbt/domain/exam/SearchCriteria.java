package knou.cbt.domain.exam;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class SearchCriteria {

    private String departmentId;
    private String subjectId;
    private String year;
    private String semester;
    private String category;

    public SearchCriteria(String departmentId, String subjectId, String year, String semester, String category) {
        this.departmentId = departmentId;
        this.subjectId = subjectId;
        this.year = year;
        this.semester = semester;
        this.category = category;
    }
}
