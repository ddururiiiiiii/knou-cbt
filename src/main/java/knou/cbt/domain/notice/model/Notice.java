package knou.cbt.domain.notice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import knou.cbt.domain.common.model.UseYn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notice {
    private Long id;
    private String title;
    private String content;
    private Boolean pinned;
    private UseYn useYn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static Notice create(Long id,
                                String title,
                                String content,
                                Boolean pinned,
                                UseYn useYn,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
        return new Notice(id,
                        title,
                        content,
                        pinned,
                        useYn,
                        createdAt,
                        updatedAt);
    }

}
