# 통계 대시보드 설계 — 현재 상태와 로그인 도입 후 확장 계획

`/admin/statistics` 관련 설계 문서. `docs/progress.md`의 `claude/statistics-dashboard-design-h2z1mc` 행에서 링크됨.

## Phase 1 — 완료 (2026-08-24, main 병합됨)

로그인 기능이 아직 없어(모든 시험 응시는 익명) 응시 데이터가 DB에 전혀 없던 상태에서 시작.

- **`exam_attempt_log` 테이블 신설** (마이그레이션 `V9`): 시험 제출 시점에 `exam_id`/`subject_id`/`subject_name`/`exam_type`/`year`/`score`/`total_count`/`elapsed_seconds`를 기록. **`user_id` 컬럼은 처음부터 nullable로 만들어뒀고 지금은 항상 NULL** — 로그인이 붙으면 이 컬럼을 채우기만 하면 되도록 미리 설계해둠.
- 로그 기록 위치: `ExamSolveController.submit()` — 채점 직후, try-catch로 감싸서 로그 저장 실패가 실제 채점·제출 흐름을 막지 않게 격리.
- 화면: `/admin/statistics` — 오늘/7일/누적 응시 수, 최근 14일 응시 추이(Chart.js), 인기 과목·시험 TOP 10(과목은 평균 정답률도), 콘텐츠 현황(학과·과목·시험·문제 수, 문제 없는 시험 목록).
- 코드 위치: `domain/statistics/**`(model·mapper·service·dto), `web/admin/StatisticsViewController`, `templates/admin/statistics/statisticsDashboard.html`, `static/js/admin-statistics.js`.

## Phase 2 — 로그인(회원가입) 도입 후에 추가할 수 있는 것

로그인이 없어서 지금은 낼 수 없는 지표들. 아래는 실제 착수 시 참고할 목록이며, 아직 설계 확정 전이라 구현 방식은 그때 다시 정하면 됨.

### 개인화 지표
- **사용자별 응시 이력·정답률 추이** — 개인 성장 그래프. `exam_attempt_log.user_id`를 채우기 시작하면 바로 집계 가능.
- **과목별 완주율** — 한 과목에 등록된 시험을 전부 풀어본 사용자 비율.
- 즐겨찾기/북마크한 과목, 최근 푼 시험 등 개인화 UI에도 같은 데이터 재사용 가능.

### 사용자 규모 지표
- **가입자 추이** (일별/누적) — `users` 테이블에 `created_at`이 이미 있어서 로그인 붙는 순간 바로 집계 가능 (추가 스키마 불필요).
- **DAU/MAU** — 현재는 "시험 응시" 이벤트만 기록해서, 응시 없이 둘러보기만 한 로그인 사용자는 못 잡음. 필요하면 로그인 성공 시점에 별도 "방문/활동" 로그를 남길지 결정 필요.
- **재방문율/리텐션 코호트** — 가입일 기준 코호트별 N일 후 재방문 비율. DAU 로그가 있어야 정확히 낼 수 있음.

### 구현 시 체크할 것
1. `exam_attempt_log`에 `user_id` 채우기 시작 (로그인 세션에서 획득) — 스키마 변경 불필요, 기존 익명 로그와도 자연스럽게 공존.
2. DAU/MAU·리텐션을 낼 거면 로그인 이벤트(또는 세션 활동) 로그 테이블을 새로 만들지, 아니면 응시 이벤트만으로 근사치를 쓸지 결정.
3. 사용자별 통계를 새로 노출하게 되면 `privacy` 개인정보처리방침에 관련 문구 추가가 필요한지 검토.
4. Phase 1의 `StatisticsMapper`/`StatisticsService`/`statisticsDashboard.html` 구조를 그대로 확장하면 됨 — 새 쿼리·카드만 추가하는 정도로 충분할 것으로 예상.

### Phase 1에서 의도적으로 미룬 것 (참고)
- GA(구글 애널리틱스) 트래픽 데이터를 관리자 화면에 끌어오는 것 — 서비스 계정 인증·쿼터 관리가 별도로 필요해 이번 범위에서 제외함. 로그인과는 무관하게 언제든 별도로 착수 가능.
