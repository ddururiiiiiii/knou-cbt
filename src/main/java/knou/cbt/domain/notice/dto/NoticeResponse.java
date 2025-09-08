package knou.cbt.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import knou.cbt.domain.notice.model.Notice;

import java.time.LocalDateTime;


public record NoticeResponse(
        Long id,
        String title,
        String content,
        Boolean pinned,
        UseYn useYn,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
){
    public static NoticeResponse of(Notice notice) {
        return  new NoticeResponse(
         notice.getId(),
         notice.getTitle(),
         notice.getContent(),
         notice.getPinned(),
         notice.getUseYn(),
         notice.getCreatedAt(),
         notice.getUpdatedAt()
        );
    }

}
