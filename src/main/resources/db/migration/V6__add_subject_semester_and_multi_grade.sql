-- 학년 다중값 지원: 하나의 과목이 여러 학년(예: 1학년+3학년)에서 개설되는 경우가 있어
-- 과목/시험 데이터를 학년별로 중복 입력하지 않아도 되도록, INT 단일값 -> 콤마구분 문자열
-- ("1,3" 형식, exam_question.answers와 동일한 패턴)로 변경한다.
ALTER TABLE subject
    ALTER COLUMN grade TYPE VARCHAR(30) USING (grade::text);

-- 학기 컬럼 추가 (한 과목은 항상 1학기/2학기 중 하나로 개설됨 -> FIRST/SECOND 별도 행)
ALTER TABLE subject
    ADD COLUMN semester VARCHAR(10) NOT NULL DEFAULT 'FIRST';

-- 과목명 전역 유일 제약을 (학과, 과목명, 학기) 조합 유일로 변경.
-- 학기가 다르면 같은 과목명이 여러 행으로 존재할 수 있어야 하기 때문.
ALTER TABLE subject DROP CONSTRAINT subject_subject_name_key;
ALTER TABLE subject ADD CONSTRAINT subject_dept_name_semester_key UNIQUE (department_id, subject_name, semester);
