package knou.cbt.domain.siteoperation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SiteOperationSetting {

    private Long id;

    private Boolean maintenanceEnabled;
    private String maintenanceMessage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime maintenanceExpectedEndAt;

    private Boolean bannerEnabled;
    private String bannerMessage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bannerScheduledAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static SiteOperationSetting of(Long id,
                                          Boolean maintenanceEnabled,
                                          String maintenanceMessage,
                                          LocalDateTime maintenanceExpectedEndAt,
                                          Boolean bannerEnabled,
                                          String bannerMessage,
                                          LocalDateTime bannerScheduledAt,
                                          LocalDateTime updatedAt) {
        return new SiteOperationSetting(id,
                maintenanceEnabled,
                maintenanceMessage,
                maintenanceExpectedEndAt,
                bannerEnabled,
                bannerMessage,
                bannerScheduledAt,
                updatedAt);
    }
}
