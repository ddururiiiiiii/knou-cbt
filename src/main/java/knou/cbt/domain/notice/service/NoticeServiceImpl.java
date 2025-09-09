package knou.cbt.domain.notice.service;

import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.notice.dto.NoticeRequest;
import knou.cbt.domain.notice.dto.NoticeResponse;
import knou.cbt.domain.notice.dto.mapper.NoticeDtoMapper;
import knou.cbt.domain.notice.exception.NoticeNotFoundException;
import knou.cbt.domain.notice.mapper.NoticeMapper;
import knou.cbt.domain.notice.model.Notice;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NoticeResponse> listPage(String keyword, String useYn, PageRequest pageRequest) {
        List<NoticeResponse> content = mapper.findAllPaged(
                        pageRequest.offset(),
                        pageRequest.sizeOrDefault(),
                        keyword,
                        useYn
                ).stream()
                .map(NoticeResponse::of)
                .toList();

        int total = mapper.countAll(keyword, useYn);
        int totalPages = (int) Math.ceil((double) total / pageRequest.sizeOrDefault());

        return new PageResponse<>(content,
                pageRequest.pageOrDefault(),
                pageRequest.sizeOrDefault(),
                total,
                totalPages);
    }

    @Override
    @Transactional(readOnly = true)
    public int count(String keyword, String useYn) {
        return mapper.countAll(keyword, useYn);
    }

    @Override
    @Transactional(readOnly = true)
    public Notice get(Long id) {
        return findNoticeOrThrow(id);
    }

    @Override
    public void create(NoticeRequest request) {
        // HTML 정화
        String safeContent = Jsoup.clean(request.getContent(), Safelist.relaxed());
        request.setContent(safeContent);
        Notice notice = NoticeDtoMapper.fromCreateRequest(request);
        mapper.insert(notice);
    }

    @Override
    public void update(Long id, NoticeRequest request) {
        // HTML 정화
        String safeContent = Jsoup.clean(request.getContent(), Safelist.relaxed());
        request.setContent(safeContent);
        findNoticeOrThrow(id);
        Notice notice = NoticeDtoMapper.fromUpdateRequest(id, request);
        mapper.update(notice);
    }

    @Override
    public void delete(Long id) {
        findNoticeOrThrow(id);
        mapper.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notice> getPinnedNotices() {
        return mapper.findPinned();
    }

    private Notice findNoticeOrThrow(Long id) {
        Notice notice = mapper.findById(id);
        if (notice == null) {
            throw new NoticeNotFoundException(id);
        }
        return notice;
    }

    @Transactional(readOnly = true)
    public List<Notice> getHomeNotices() {
        List<Notice> result = new ArrayList<>();

        // 1. 고정 공지 모두 추가
        result.addAll(mapper.findPinnedAll());

        // 2. 일반 공지 5개 추가
        result.addAll(mapper.findRecentNormal(5));

        return result;
    }

}
