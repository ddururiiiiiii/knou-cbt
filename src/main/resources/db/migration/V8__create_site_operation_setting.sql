-- 배포/점검 운영 설정 (싱글 로우)
CREATE TABLE site_operation_setting (
    id BIGINT PRIMARY KEY,
    maintenance_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    maintenance_message VARCHAR(500),
    maintenance_expected_end_at TIMESTAMP,
    banner_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    banner_message VARCHAR(500),
    banner_scheduled_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO site_operation_setting (id, maintenance_enabled, banner_enabled)
VALUES (1, FALSE, FALSE);
