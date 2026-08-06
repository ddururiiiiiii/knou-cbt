package knou.cbt.domain.siteoperation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SiteOperationSettingRequest {

    public static final DateTimeFormatter FORM_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private boolean maintenanceEnabled;
    private String maintenanceMessage;
    private String maintenanceExpectedEndAt;

    private boolean bannerEnabled;
    private String bannerMessage;
    private String bannerScheduledAt;

    public LocalDateTime parseMaintenanceExpectedEndAt() {
        return parse(maintenanceExpectedEndAt);
    }

    public LocalDateTime parseBannerScheduledAt() {
        return parse(bannerScheduledAt);
    }

    private LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value, FORM_DATETIME_FORMAT);
    }
}
