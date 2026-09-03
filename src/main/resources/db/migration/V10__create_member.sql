-- 회원(소셜 로그인) 테이블. 관리자용 users 테이블과는 완전히 분리한다.
-- 식별자는 이메일이 아니라 (provider, provider_id) 조합 — 카카오/네이버는 이메일이 선택 동의 항목이라
-- 항상 온다는 보장이 없고, 나중에 바뀔 수도 있어서 유저 식별용으로는 부적합하기 때문.
CREATE TABLE member (
    id BIGSERIAL PRIMARY KEY,
    provider VARCHAR(20) NOT NULL,
    provider_id VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    nickname VARCHAR(100) NOT NULL,
    department_id BIGINT REFERENCES department (id),
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (provider, provider_id)
);

CREATE INDEX idx_member_department_id ON member (department_id);

-- exam_attempt_log.user_id는 V9에서 이미 nullable로 마련해둔 자리라 스키마 변경 없이 그대로 채워 쓴다.
-- FK를 걸지 않는 이유: 회원 탈퇴 시 member 행은 삭제하되 exam_attempt_log.user_id는 NULL로 되돌려
-- 통계 집계(응시 수 등)는 유지하면서 개인 식별만 지우는 방식(비식별화)을 쓰기 때문 — 애플리케이션에서 직접 관리한다.
COMMENT ON COLUMN exam_attempt_log.user_id IS '로그인 회원(member.id). 비회원 응시는 NULL, 회원 탈퇴 시에도 NULL로 되돌려 비식별화';
