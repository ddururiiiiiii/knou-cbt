# 세션 진행 상황 트래커

> 여러 Claude Code 세션(웹/로컬)이 병렬로 작업할 때 서로 컨텍스트를 공유하기 위한 문서입니다.
> 각 세션은 메모리를 공유하지 않으므로, 설계안·진행 상황·다음 할 일을 여기에 기록해두면
> 다른 세션(다른 브랜치, 로컬 터미널 포함)에서 이어서 작업할 수 있습니다.

## 사용법

1. 작업을 시작하거나 끝낼 때 아래 표에 자신의 브랜치 행을 추가/갱신하세요.
2. 설계안처럼 내용이 긴 것은 이 파일에 직접 쓰지 말고 `docs/design/<주제>.md` 파일로 따로 만든 뒤,
   이 표의 "설계 문서" 칸에 경로를 링크하세요.
3. 이 파일은 **main 브랜치에 직접** 커밋합니다 (기능 브랜치에 두면 다른 세션이 못 봄).
   - `git checkout main && git pull origin main`
   - 표 수정 후 `git add docs/progress.md && git commit -m "docs: progress 업데이트" && git push origin main`
   - 작업하던 브랜치로 다시 돌아가는 것 잊지 마세요: `git checkout <내 브랜치>`
4. 로컬 터미널에서 이어서 작업할 때는 `git fetch origin main && git show origin/main:docs/progress.md`
   또는 `git pull origin main` 후 이 파일을 열어서 현재 상황을 파악하세요.

## 세션 목록

| 브랜치 | 목적 | 상태 | 설계 문서 | 다음 할 일 | 마지막 업데이트 |
|---|---|---|---|---|---|
| `claude/memory-cli-workflow-8847cj` | 세션 간 작업 공유 워크플로우 정리 (이 파일 자체) | 완료 | - | - | 2026-08-06 |
| `claude/beta-notice-message-update-xglihe` | 메인화면 Beta 안내 문구에 오탈자/버그 양해 문구 추가 및 줄바꿈 정리 (`index.html`) | 완료 | - | - | 2026-08-06 |
| `claude/notice-detail-top-buttons-v0klmh` | 공지사항 상세 화면(`noticeDetail.html`)에 하단에만 있던 수정/삭제/목록 버튼을 상단에도 동일하게 추가 | 완료 | - | - | 2026-08-06 |
| `claude/notice-post-guidelines-iebqwe` | 실제 공지사항 게시판에 올릴 글감 기획 및 초안 작성 — 서비스 소개, 이용 매뉴얼, 버그·오타 제보(+면책 문구), 기출자료 제공 요청 4종 + 향후 반복 게시할 기출/기능 업데이트 소식용 제목·본문 템플릿. 코드 변경 없이 대화로 콘텐츠만 작성(웹 에디터에 직접 붙여넣는 방식) | 완료 | - | 작성된 4개 초안을 실제 공지사항 웹 에디터에 붙여넣고 화면 캡처 이미지 삽입 후 게시 | 2026-08-06 |
| `claude/deployment-user-screen-nl15mb` | 정기/긴급 배포 시 점검 모드 및 사전 공지 배너 기능 추가. DB 기반 점검 플래그를 관리자 페이지(`/admin/maintenance`)에서 즉시 토글, 인터셉터가 점검 중 요청을 안내 화면(503)으로 전환하되 ROLE_ADMIN 세션과 이미 응시 중이던 시험의 최종 제출(`POST /exams/{id}/solve`)은 통과시킴. 정기점검 사전 공지용 배너를 메인 레이아웃 상단에 별도 노출. 배포 후 실서비스에서 설정 저장 시 500 에러 발생 — `SiteOperationSetting`에 Lombok getter(`getMaintenanceEnabled`)와 직접 만든 `isMaintenanceEnabled()`가 같은 프로퍼티에 공존해 MyBatis가 getter를 특정 못 하던 버그였고, 헬퍼 메서드 제거 + 회귀 테스트 추가로 수정. 이후 점검 화면이 부트스트랩 기본 스타일이라 브랜드가 안 느껴진다는 피드백을 받아, 메인 화면과 동일한 인디고 포인트 컬러/카드 톤/잉크 네비·풋터로 리디자인(브랜드 로고, 점검중 배지, 예상 종료 시각 배지, 60초 자동 새로고침 포함). 모두 로컬 Postgres+헤드리스 크롬 스크린샷으로 검증 후 main에 병합·배포 완료 | 완료 | - | - | 2026-08-06 |
| `claude/remove-roadmap-md-b1ytt3` | `ROADMAP.md` 삭제 및 `CLAUDE.md`의 참조 문구 정리 | 완료 | - | - | 2026-08-06 |
| `main` (로컬) | 관리자 UI 개선 5건: (1) 문제 없는 시험 "문제 풀기" 클릭 시 alert 대신 커스텀 토스트(`appToast`, `fragments/dialogs.html`+`ui-dialog.js`+`app.css`) 안내, (2) 엑셀 문제 업로드 시 파일 내 시험ID 불일치·현재 화면 시험ID와 불일치·문제번호 누락이면 `AdminExamQuestionViewController`에서 차단, (3) `solve.html`/`preview.html` 보기 원형 숫자(①②③④) 크기 확대 및 텍스트·이미지 보기 스타일 통일, (4) 관리자 시험 목록(`exam/examList.html`, "시험 보기" 화면)에 미사용 시험만 보기 체크박스 추가(서버는 이미 `useYn` 파라미터 지원 중이었음), (5) 과목/학과/시험관리 폼의 사용여부 select를 라디오 버튼으로 변경. 로컬 Docker Postgres + `bootRun`으로 관리자 세션 로그인 후 curl로 각 화면 렌더링·엑셀 업로드 3종 시나리오 직접 검증 완료(Claude-in-Chrome 확장 미연결로 스크린샷 대신 서버 응답 HTML로 확인) | 완료 | - | - | 2026-08-07 |
| `claude/pre-deployment-documentation-3me0m2` | 9/1 정식 배포 전 개발문서(내부)·기술블로그(공개) 소재 정리 및 초안 작성. `docs/writing-topics.md`를 Tier1(배포 전 필수)/Tier2(설계·트러블슈팅 로그, 내부용)/Tier3(공개 블로그)로 우선순위 재정리. 코드·커밋 기록(마이그레이션 파일, `GlobalExceptionHandler`, `LoginAttemptService`, `SiteOperationSetting` 등)을 실제로 조사해 근거 있는 초안을 `docs/drafts/`에 12건 작성: Tier1 7건(배포 전 체크리스트, 스모크 테스트, Sentry 모니터링, 관리자 계정 매뉴얼, DB 백업/복구, Render GitHub App 재연결, 점검모드 매뉴얼) 전부, Tier2 5건(YAML multipart 트러블슈팅, 로컬 개발환경 가이드, 예외처리 컨벤션, use_yn 계층 설계, DB 마이그레이션 의사결정 로그) 완료. 조사 중 `application-prod.yml`에 sentry 설정 섹션이 누락된 것을 발견해 체크리스트에 반영함 | 진행 중 | - | Tier2 나머지 5건(Flyway+Supabase Pooler, 스키마-코드 불일치, Import 검증 원칙, 개발 로테이션, 운영비 트래킹)은 당시 기억 의존이라 미착수. Tier3(블로그 23건)는 착수 전 — `docs/writing-topics.md`의 "추천 착수 순서" 1~5번부터 이어서 쓰면 됨. 초안이 실제 게시판/블로그에 옮겨지면 `docs/drafts/`는 삭제 가능 | 2026-08-07 |

| `claude/readme-remove-broken-images-xwze64` | `README.md`의 "📸 화면 예시" 섹션 제거 — Kakao/Tistory CDN 이미지 링크(`expires` 파라미터 만료)가 전부 엑박으로 뜨는 문제 해결. 이미지 없이는 항목 텍스트만 남아 의미가 없어 해당 섹션 전체(사용자/관리자 화면 캡처 목록) 삭제 | 완료 | - | - | 2026-08-11 |
| `claude/custom-domain-update` (main에 병합·배포 완료) | 4주차 로드맵 착수: 가비아에서 `knou-cbt.com` 구매 + Render 커스텀 도메인 연결(A 레코드 `@`/`www` → `216.24.57.1`, root→www 301 리다이렉트, SSL은 Google Trust Services로 자동 발급, 연결 직후 www 엣지 라우팅 전파에 몇 분 소요됐으나 정상화 확인). `robots.txt`/`sitemap.xml`의 `knou-cbt.onrender.com` 참조를 `www.knou-cbt.com`으로 교체해 커밋·main 병합·푸시까지 완료. 진행 중 사용자가 "knou-cbt.com 도메인이 방통대(KNOU) 상표/저작권과 법적 문제 없는지" 질문 → 조사 결과 (1) 도메인 자체는 `부정경쟁방지법` 사이버스쿼팅 조항 소지가 있으나 이미 `knou-cbt` 저장소명/패키지명으로 공개돼 있어 증분 리스크는 낮음 (2) **기출문제 원문 저작권이 더 핵심 리스크** — 대법원 판례상 시험문제는 창작성 인정되는 저작물이고 "영리 목적 재배포는 면책 안 됨"이 확립돼 있음, 다만 동일 컨셉(방통대 기출 + 애드센스)의 개인 사이트(`allaclass.tistory.com`)가 오랫동안 문제없이 운영 중인 게 확인돼 실질적 단속 리스크는 낮다고 판단, 사용자가 진행 결정. GA 대시보드에 새 도메인 스트림 추가는 코드 변경 아니라 사용자가 직접 해야 함(미완료) | 완료 | - | 로드맵 4주차 남은 항목: 애드센스 신청(위 저작권 리스크 인지 상태로 진행할지 재확인 권장), Sentry 알림 이메일 실제 발송 테스트, GA 정상 수집 확인(+새 도메인 스트림 등록) | 2026-08-16 |

<!-- 새 세션에서 작업을 시작하면 위 표에 행을 추가하세요. 상태 예시: 설계 중 / 구현 중 / 리뷰 대기 / 완료 -->
