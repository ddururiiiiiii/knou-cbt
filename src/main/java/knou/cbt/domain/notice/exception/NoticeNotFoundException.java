package knou.cbt.domain.notice.exception;

public class NoticeNotFoundException extends RuntimeException {

    public NoticeNotFoundException(Long id) {
        super("공지사항을 찾을 수 없습니다. id=" + id);
    }
}
