package knou.cbt.domain.subject.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.subject.dto.SubjectRequest;
import knou.cbt.domain.subject.dto.SubjectResponse;

public interface SubjectService {

    PageResponse<SubjectResponse> listPage(
            String keyword,
            String useYn,
            PageRequest pageRequest
    );

    int count(String keyword, String useYn);

    SubjectResponse get(Long id);

    void create(SubjectRequest request);

    void update(Long id, SubjectRequest request);

    void delete(Long id);
}
