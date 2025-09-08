package knou.cbt.domain.notice.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.notice.dto.NoticeRequest;
import knou.cbt.domain.notice.dto.NoticeResponse;
import knou.cbt.domain.notice.model.Notice;

import java.util.List;

public interface NoticeService {

    PageResponse<NoticeResponse> listPage(String keyword, String useYn, PageRequest pageRequest);
    int count(String keyword, String useYn);
    Notice get(Long id);
    void create(NoticeRequest req);

    void update(Long id, NoticeRequest req);

    void delete(Long id);

    List<Notice> getPinnedNotices();

    List<Notice> getHomeNotices();
}
