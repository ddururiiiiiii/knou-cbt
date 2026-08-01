-- ⚠️ 운영(Supabase) DB 전용 일회성 정리 스크립트 — Flyway 마이그레이션 아님, 직접 SQL 에디터에서 검토 후 실행할 것
-- 실행 전 반드시 백업/스냅샷 확보 권장 (Supabase Dashboard > Database > Backups)
--
-- 삭제 대상: exam, exam_question, exam_question_answer, subject 전체
-- 유지 대상: department, users(관리자 계정)

BEGIN;

-- exam 삭제 시 exam_question -> exam_question_answer는 ON DELETE CASCADE로 자동 삭제됨
DELETE FROM exam;

-- exam이 먼저 비워져야 subject 삭제 가능 (fk_exam_subject, CASCADE 없음)
DELETE FROM subject;

-- 삭제 후 상태 확인
SELECT
    (SELECT COUNT(*) FROM exam)         AS exam_count,
    (SELECT COUNT(*) FROM exam_question) AS exam_question_count,
    (SELECT COUNT(*) FROM subject)      AS subject_count,
    (SELECT COUNT(*) FROM department)   AS department_count_should_be_unchanged,
    (SELECT COUNT(*) FROM users)        AS users_count_should_be_unchanged;

-- 위 결과 확인 후 문제 없으면:
COMMIT;
-- 잘못됐으면 COMMIT 대신:
-- ROLLBACK;
