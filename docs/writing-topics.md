# 기록용 개발문서 / 기술블로그 소재 목록

> 로컬 CLI 세션의 메모리를 Claude Code 웹 세션과 공유하기 위한 임시 파일입니다.
> 글쓰기 작업이 다 끝나면 이 파일과 `docs/drafts/`는 삭제해도 됩니다.
> (2026-07-30 최초 정리, 2026-08-07 우선순위 정리 → Tier1 전체 + Tier2 5건 초안 작성 완료, 2026-08-17 Tier1 일부 실사용 검증으로 빈칸 채움 + Tier2 신규 2건 추가)

## 우선순위 정리 기준
- **Tier 1 (배포 전 필수)**: 9/1 정식 운영 전환에 실제로 필요한 운영 문서. 없으면 배포 당일/직후에 곤란해지는 것들.
- **Tier 2 (배포 전 기록 권장)**: 지금 기록 안 해두면 기억이 휘발되는 설계/트러블슈팅 로그. 배포와 직접 관련은 없지만 "지금이 적기". **내부용(비공개)** — Tier 1과 같은 카테고리.
- **Tier 3 (배포 후에도 무방)**: 공개 기술 블로그. 순서 상관없이 운영 안정화 이후 천천히 써도 됨.

## 지금 세션에서 한 작업 (CLI에서 이어받을 때 참고)
- 웹 세션에서 Tier 1(7개) 전부 + Tier 2(5개) 초안을 채팅으로 작성 → `docs/drafts/` 밑에 마크다운 파일로 저장해둠
- 각 초안은 코드/커밋 기록을 실제로 조사해서 근거 있는 내용으로 작성함 (빈칸 표시된 부분은 직접 겪은 사람 기억 필요)
- 2026-08-17 로컬 세션: 도메인 구매·Sentry·애드센스·DB 백업 리허설 작업을 실제로 진행하면서 나온 결과물로 `tier1-05`의 빈칸(백업 정책 확인, 복원 리허설)을 채우고, 신규 Tier2 2건(`tier2-06` Sentry logback 누락, `tier2-07` 정적파일 permitAll 누락)을 추가 작성함
- 다음 시작 지점: Tier2 나머지 5건(여유될 때) 또는 **Tier 3(블로그) 작성**, 추천 순서는 아래 Tier 3 섹션 참고

---

## 📁 Tier 1 — 배포 전 필수 (내부 운영문서) — ✅ 7/7 초안 완료
8월 3주차(늦어도 8/24)까지 완료 목표. 실제 운영 시작 시 바로 참조할 것들이라 우선순위 최상위.

- [x] 배포 전 체크리스트 → [`docs/drafts/tier1-01-pre-deploy-checklist.md`](drafts/tier1-01-pre-deploy-checklist.md)
- [x] 배포 전 스모크 테스트 체크리스트 → [`docs/drafts/tier1-02-smoke-test-checklist.md`](drafts/tier1-02-smoke-test-checklist.md)
- [x] 모니터링 도구 구성 (Sentry) → [`docs/drafts/tier1-03-sentry-monitoring.md`](drafts/tier1-03-sentry-monitoring.md) — prod.yml에 sentry 설정 누락된 것 발견, 배포 전 확인 필요 항목으로 반영함
- [x] 관리자 계정 운영 매뉴얼 → [`docs/drafts/tier1-04-admin-account-manual.md`](drafts/tier1-04-admin-account-manual.md)
- [x] DB 백업/복구 리허설·롤백 절차 → [`docs/drafts/tier1-05-db-backup-restore.md`](drafts/tier1-05-db-backup-restore.md) — 2026-08-17 실제 리허설 진행 후 빈칸 채움: 무료 플랜은 백업 자체가 없다는 사실 확인, 로컬 복원+forward-fix 마이그레이션 실제 검증 완료. Render 롤백만 아직 미실행으로 남음
- [x] Render GitHub App 연결 끊김 원인/재연결 절차 → [`docs/drafts/tier1-06-render-github-app-reconnect.md`](drafts/tier1-06-render-github-app-reconnect.md) — 실제 원인은 빈칸, 기억나면 채워넣기
- [x] 점검 모드(유지보수) 기능 사용법 → [`docs/drafts/tier1-07-maintenance-mode-manual.md`](drafts/tier1-07-maintenance-mode-manual.md)

> 참고: Sentry 알림이 실제로 왜 안 왔는지(근본원인)는 이 문서(`tier1-03`)가 아니라 Tier2 신규 문서 `tier2-06`에 정리함 — `sentry-logback` 모듈 누락 문제.

## 📁 Tier 2 — 배포 전 기록 권장 (설계/트러블슈팅 로그, 내부용) — 7/12 초안 완료
순서 무관, 각 이슈를 해결한 세션이 있다면 그때그때 짧게 기록.

- [x] YAML 설정 트러블슈팅 노트 (multipart 무시 사례) → [`docs/drafts/tier2-01-yaml-multipart-troubleshooting.md`](drafts/tier2-01-yaml-multipart-troubleshooting.md) — 실제 커밋(`87e6376`→`55d6ec6`) 근거로 작성
- [x] 로컬 개발환경 구성 가이드 → [`docs/drafts/tier2-02-local-dev-guide.md`](drafts/tier2-02-local-dev-guide.md)
- [x] 예외 처리 (전역 예외 핸들러 컨벤션, 에러 코드 체계) → [`docs/drafts/tier2-03-exception-handling-convention.md`](drafts/tier2-03-exception-handling-convention.md)
- [x] 계층적 사용여부(use_yn) 로직 설계 노트 → [`docs/drafts/tier2-04-use-yn-hierarchical-design.md`](drafts/tier2-04-use-yn-hierarchical-design.md)
- [x] DB 마이그레이션 의사결정 로그 (V1~V8) → [`docs/drafts/tier2-05-migration-decision-log.md`](drafts/tier2-05-migration-decision-log.md)
- [x] Sentry 알림이 전혀 안 오던 이유 (`sentry-logback` 누락) → [`docs/drafts/tier2-06-sentry-logback-missing-integration.md`](drafts/tier2-06-sentry-logback-missing-integration.md) — 2026-08-17 세션에서 새로 발생한 실제 트러블슈팅, 커밋 `912e5a8`→`511f0be`→`7e336fb` 근거
- [x] robots.txt/sitemap.xml/ads.txt가 로그인 화면으로 리다이렉트되던 버그 → [`docs/drafts/tier2-07-static-file-security-permitall.md`](drafts/tier2-07-static-file-security-permitall.md) — 2026-08-17 세션에서 애드센스 인증 작업 중 발견, 커밋 `f32cdbd` 근거
- [ ] Flyway + Supabase Connection Pooler 이슈 노트 — 증상/에러메시지 기억 필요, 미착수
- [ ] 스키마-애플리케이션 로직 불일치 체크리스트 — 구체적 컬럼 사례 기억 필요, 미착수 (V7 마이그레이션 로그와 연결 가능)
- [ ] 가져오기(Import) 기능 검증 원칙 확정본 — 설계 판단 히스토리 기억 필요, 미착수
- [ ] 개발 로테이션 (Phase 0~4, 우선순위 기준) — 회고 의존, 미착수. Tier3 "Phase 0~4 회고" 블로그와 재료 겹침
- [ ] 개인 프로젝트 월별 운영비 트래킹 — 지금은 틀만, 실제 수치는 운영 이후 누적, 미착수

> 참고: "Render 자동배포 웹훅 트러블슈팅 로그"는 Tier1 "Render GitHub App 재연결 절차" 문서로 흡수·통합함 (중복 제거).
> 참고: 커스텀 도메인 구매+상표/저작권 리스크 검토 로그, 애드센스 계정 정책(중복계정 금지) 등도 2026-08-17 세션에서 새로 나온 소재지만, 이번엔 목록/초안에서 제외함(사용자 요청).

---

## ✍️ Tier 3 — 배포 후에도 무방 (공개 기술 블로그) — 미착수
"문제→시도→해결→교훈" 스토리텔링. 배포 전에 미리 초안 잡아둬도 좋지만 급하지 않음.

### 추천 착수 순서 (코드/기록 근거 있는 것부터)
1. (짧은 팁 글) "Spring Security `.roles()`가 이미 `ROLE_` 접두사를 붙여준다" — `CustomUserDetailsService` 코드 근거 있음, 짧고 워밍업하기 좋음
2. "Lombok 게터와 내가 만든 게터가 충돌해서 MyBatis가 헷갈렸던 이야기" — `docs/progress.md`에 실제 사건 기록 있음 (`SiteOperationSetting`의 `getMaintenanceEnabled` vs `isMaintenanceEnabled` 충돌)
3. "미사용으로 껐는데 왜 계속 보이지?" — 계층적 노출 제어 발견/설계기 — `tier2-04-use-yn-hierarchical-design.md` 내용을 스토리텔링으로 재구성
4. 관리자 로그인 브루트포스 방지 구현기 — `LoginAttemptService`/`LoginAttemptFilter` 코드 근거 있음
5. 개인 프로젝트에 Sentry 붙이기 — `tier1-03-sentry-monitoring.md` 조사 내용 재활용

### 지금 써도 되는 것 (그 외, 이미 해결된 이슈)
- Render + Supabase 조합으로 개인 프로젝트 배포하기
- 로그인 방식 설계기 (세션 vs JWT 등 인증 방식 선택 근거)
- PDF 기출문제 자동 추출 파이프라인 만들기
- Render 자동배포가 갑자기 안 됐던 이야기
- Flyway + Supabase 커넥션 풀러 충돌 해결기
- PostgreSQL 한글 정렬 버그 잡은 이야기
- CSP 도입 후 버튼이 안 눌리던 문제 해결기
- N+1 쿼리 최적화기
- 공지사항 미인증 CRUD 취약점 발견/수정기
- PDF 자동화를 포기하고 수기 입력으로 전환한 이유
- "아무도 신고하지 않은 버그: 문제 이미지가 저장할 때마다 조용히 사라지던 이야기"
- "이미지는 저장됐는데 화면엔 안 보이는 이유" — CSP img-src + Supabase RLS 이중 원인 디버깅기
- "프레임워크 없이 커스텀 Alert/Confirm/Toast 만들기"
- "가져오기 기능, 어디까지 엄격하게 검증해야 할까" — 엄격→관대→부분적으로 다시 엄격, 왔다갔다한 설계 판단 회고

### 운영 데이터/경험이 쌓인 뒤 쓰는 게 더 좋은 것 (9/1 이후로 미루기)
- 1인 개발로 방통대 CBT 서비스 만들며 배운 것들 (Phase 0~4 회고) — 정식 배포까지 겪은 걸 포함해서 쓰면 더 완결됨
- 사이드 프로젝트에서 놓치기 쉬운 것들 (개인정보처리방침, SEO 등)
- 개인 프로젝트 실제 운영비 공개 — 최소 1~2개월 실 운영비 수치 필요
- 1인 개발자의 유료 오픈 로드맵 관리기 — 실제 로드맵 운영 경험 쌓인 뒤

---

## ✅ Tier 3 — 19/19 작성 완료 (2026-08-07)
`docs/blog/` 아래 19개 마크다운 파일로 저장함. 코드/git log를 실제로 조사해서 근거 있는 내용으로 작성.

- [x] 01-spring-security-roles-prefix.md
- [x] 02-lombok-mybatis-ambiguous-getter.md
- [x] 03-hierarchical-use-yn-visibility.md
- [x] 04-admin-login-bruteforce-protection.md
- [x] 05-adding-sentry-to-side-project.md
- [x] 06-render-supabase-deployment.md
- [x] 07-session-vs-jwt-auth-design.md
- [x] 08-pdf-exam-extraction-pipeline.md
- [x] 09-render-auto-deploy-webhook-incident.md — 최초엔 git 근거만으로 `[ ]` 빈칸 초안 작성했으나, `project_roadmap` 메모리에 실제 원인(GitHub App 연결 끊김)·해결 절차 전부 있어서 채워 넣어 완결함
- [x] 10-flyway-supabase-connection-pooler.md — 위와 동일하게 `project_roadmap` 메모리에서 실제 에러(`prepared statement "S_2" already exists`)·해결책(`prepareThreshold=0`) 확인 후 완결함
- [x] 11-postgresql-korean-collation-bug.md
- [x] 12-csp-inline-script-button-bug.md
- [x] 13-n-plus-one-query-optimization.md
- [x] 14-notice-unauthenticated-crud-vulnerability.md
- [x] 15-giving-up-pdf-automation.md — `project_roadmap` 메모리에서 확정된 결정(2026-07-30, 수기 전환)과 같은 날 있었던 V5 데이터품질 사고를 근거로 완결함
- [x] 16-question-image-disappearing-bug.md
- [x] 17-image-uploaded-but-not-visible-csp.md — 처음엔 코드에 흔적 없다고 "RLS 근거없음"으로 잘못 정정했다가, `project_roadmap` 메모리에서 실제 RLS 원인(anon 키→service_role 키 교체, Render 환경변수라 git엔 안 남음) 확인 후 원래 "CSP+RLS 이중원인" 소재로 재정정함
- [x] 18-custom-alert-confirm-toast.md
- [x] 19-import-validation-strictness-retrospective.md

## 다음 액션
1. Tier 2 나머지 5건은 관련 세션 작업할 때 곁다리로 기록 (별도 시간 안 내도 됨)
2. 19편 전부 빈칸 없이 완결됨 — `docs/blog/*.md` 그대로 다듬어서 발행 가능
3. `docs/drafts/*.md`, `docs/blog/*.md`는 실제 게시판/블로그에 옮겨적은 뒤엔 삭제해도 무방 (임시 저장용)
