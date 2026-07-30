-- extractor/upload.py가 한글 원문("전공필수" 등)을 그대로 저장해
-- SubjectCategory enum(MAJOR/LIBERAL/ELECTIVE)과 불일치가 발생한 기존 데이터를 정리
UPDATE subject SET subject_category = 'MAJOR'    WHERE subject_category IN ('전공필수', '전공선택', '전공');
UPDATE subject SET subject_category = 'LIBERAL'  WHERE subject_category IN ('교양필수', '교양선택', '교양');
UPDATE subject SET subject_category = 'ELECTIVE' WHERE subject_category IN ('일반선택');