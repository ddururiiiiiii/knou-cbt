-- 시험 응시(제출) 로그: 통계 화면용. 로그인 기능 도입 전이라 익명으로 기록하고,
-- user_id는 지금은 항상 NULL로 남겨두되 나중에 로그인이 붙으면 채워 쓸 자리로 미리 마련해둔다.
CREATE TABLE exam_attempt_log (
    id BIGSERIAL PRIMARY KEY,
    exam_id BIGINT NOT NULL REFERENCES exam (id),
    subject_id BIGINT NOT NULL REFERENCES subject (id),
    subject_name VARCHAR(100) NOT NULL,
    exam_type VARCHAR(20) NOT NULL,
    year INT NOT NULL,
    score INT NOT NULL,
    total_count INT NOT NULL,
    elapsed_seconds INT,
    user_id BIGINT,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_exam_attempt_log_exam_id ON exam_attempt_log (exam_id);
CREATE INDEX idx_exam_attempt_log_subject_id ON exam_attempt_log (subject_id);
CREATE INDEX idx_exam_attempt_log_submitted_at ON exam_attempt_log (submitted_at);
