package knou.cbt.domain.siteoperation.exception;

import java.time.LocalDateTime;

public class MaintenanceModeException extends RuntimeException {

    private final String maintenanceMessage;
    private final LocalDateTime expectedEndAt;

    public MaintenanceModeException(String maintenanceMessage, LocalDateTime expectedEndAt) {
        super("서비스 점검 중입니다.");
        this.maintenanceMessage = maintenanceMessage;
        this.expectedEndAt = expectedEndAt;
    }

    public String getMaintenanceMessage() {
        return maintenanceMessage;
    }

    public LocalDateTime getExpectedEndAt() {
        return expectedEndAt;
    }
}
