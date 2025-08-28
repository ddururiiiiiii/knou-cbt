package knou.cbt.domain.exam;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class SearchResult {

    private String searchKey;
    private String searchValue;
}
