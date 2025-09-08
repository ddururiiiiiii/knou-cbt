package knou.cbt.domain.notice.dto.mapper;

import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.notice.dto.NoticeRequest;
import knou.cbt.domain.notice.model.Notice;

public class NoticeDtoMapper {

    private NoticeDtoMapper() {}

    public static Notice fromCreateRequest(NoticeRequest request) {
        return Notice.create(
                null,
                request.getTitle(),
                request.getContent(),
                request.getPinned(),
                UseYn.Y,
                null,
                null
        );
    }

    public static Notice fromUpdateRequest(Long id, NoticeRequest request) {
        return Notice.create(
                id,
                request.getTitle(),
                request.getContent(),
                request.getPinned(),
                request.getUseYn(),
                null,
                null
        );
    }
}
