-- ExamQuestionServiceImpl.saveAll()이 빈 보기 값을 blankToNull()로 NULL 처리해서 저장하는데,
-- DB 컬럼은 여전히 NOT NULL이라 보기 4개를 다 안 채운 문제(또는 빈 값이 섞인 엑셀 업로드 행)를
-- 저장하려 하면 그대로 500 에러가 났음. 애플리케이션 로직/템플릿은 이미 보기가 비어있을 수
-- 있다고(option1이 null일 수 있다고) 가정하고 있어서, 그에 맞춰 스키마의 NOT NULL 제약을 제거한다.
ALTER TABLE exam_question ALTER COLUMN option1 DROP NOT NULL;
ALTER TABLE exam_question ALTER COLUMN option2 DROP NOT NULL;
ALTER TABLE exam_question ALTER COLUMN option3 DROP NOT NULL;
ALTER TABLE exam_question ALTER COLUMN option4 DROP NOT NULL;
