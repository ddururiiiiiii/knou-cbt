# CLAUDE.md

## 프로젝트 개요
KNOU CBT — 한국방송통신대학교 학생을 위한 비공식 전자 기출문제집 서비스.
Spring Boot 3.2.6 / Java 17 / MyBatis / Thymeleaf / Spring Security, PostgreSQL(Supabase), Render 배포.
자세한 내용은 `README.md`, 운영 전환 계획은 `ROADMAP.md` 참고.

## 세션 간 작업 공유 (중요)
이 저장소는 여러 Claude Code 세션(웹/로컬, 서로 다른 브랜치)에서 병렬로 작업됩니다.
세션끼리는 메모리를 공유하지 않으므로, **작업을 시작하거나 끝낼 때마다 `docs/progress.md`를 확인/갱신하세요.**

- 작업 시작 전: `git fetch origin main`으로 `docs/progress.md`를 열어 다른 세션들이 뭘 하고 있는지, 내가 이어받을 내용이 있는지 확인
- 작업 종료 시: main 브랜치로 체크아웃해서 `docs/progress.md`의 표에 내 브랜치 행을 추가/갱신하고 커밋·푸시 (`git checkout main && git pull origin main && ... && git push origin main`), 그 후 원래 작업 브랜치로 복귀
- 설계안처럼 긴 내용은 `docs/design/<주제>.md`로 따로 만들고, 표에는 경로만 링크

자세한 규칙은 `docs/progress.md` 상단 "사용법" 섹션 참고.
