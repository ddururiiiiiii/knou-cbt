# knou-cbt 운영 전환 로드맵

> 작성일: 2026-05-11  
> 목표: 사이드 프로젝트 → 실 운영 서비스 전환

---

## 현재 스택 요약
- **앱 서버:** Spring Boot 3.2.6 / Java 17 / MyBatis / Thymeleaf / Spring Security
- **DB/Storage:** Supabase (PostgreSQL + Storage)
- **배포:** Render (앱) + Supabase (DB)
- **현재 플랜:** 둘 다 무료 → 콜드 스타트, 커넥션 풀 부족 문제

---

## Phase 0 — 버그 수정 · 환경 분리 (1~3일, 비용 $0)

### 0-1. P1 버그 수정 (즉시)

#### ① GlobalExceptionHandler 로깅 추가
- **파일:** `src/main/java/knou/cbt/global/exception/GlobalExceptionHandler.java`
- **문제:** 모든 예외를 잡아 500 페이지만 반환, 로그 없음 → 운영 중 에러 원인 파악 불가
- **수정:**
```java
// Slf4j 어노테이션 추가 + log.error 한 줄 추가
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        log.error("Unhandled exception", ex);  // 이 줄 추가
        model.addAttribute("message", "알 수 없는 오류가 발생했습니다.");
        return "error/500";
    }
}
```

#### ② 세션키 고정으로 인한 답안 뒤섞임 버그
- **파일:** `src/main/java/knou/cbt/web/exam/ExamSolveController.java`
- **문제:** `"userAnswers"` 고정 키 → 탭 여러 개에서 다른 시험 풀면 답안 덮어쓰임
- **수정:** `submit()`과 `review()` 메서드에서
```java
// Before
session.setAttribute("userAnswers", answers);
List<String> answers = (List<String>) session.getAttribute("userAnswers");

// After
session.setAttribute("userAnswers_" + examId, answers);
List<String> answers = (List<String>) session.getAttribute("userAnswers_" + examId);
```

#### ③ NullPointerException 위험
- **파일:** `src/main/java/knou/cbt/web/exam/ExamSolveController.java`
- **문제:** `answers`가 null일 때 `while (answers.size() < ...)` → NPE
- **수정:** submit() 메서드 상단에 추가
```java
if (answers == null) answers = new ArrayList<>();
```

---

### 0-2. 환경 분리 (local / dev / prod)

#### 현재 구조
```
application.yml         ← 공통 설정 (HikariCP 등)
application-dev.yml     ← Supabase DB + 개발용
application-prod.yml    ← Supabase DB + 운영용
```

#### 목표 구조
```
application.yml         ← 공통 설정
application-local.yml   ← 로컬 PostgreSQL, Flyway 활성, 로그 DEBUG
application-dev.yml     ← Supabase DB (개발 스키마), Flyway 활성, 로그 DEBUG
application-prod.yml    ← Supabase DB (운영), Flyway 비활성(수동), 로그 INFO
```

#### application-local.yml 신규 생성
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/knou_cbt_local
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  flyway:
    enabled: true
  thymeleaf:
    cache: false

logging:
  level:
    root: DEBUG
    knou.cbt: DEBUG

supabase:
  url: http://localhost:54321   # Supabase CLI 로컬 실행 시
  key: local-dev-key
  bucket:
    notice: notice
    exam: exam
```

#### IntelliJ 실행 프로파일 설정 방법
- Run/Debug Configurations → Active profiles: `local`
- 환경변수 필요 없이 로컬 PostgreSQL만 있으면 됨

---

### 0-3. P2 버그 수정

#### ④ question_text VARCHAR(500) → TEXT 변경
- **파일:** `src/main/resources/db/migration/V2__alter_question_text.sql` (신규 생성)
```sql
ALTER TABLE exam_question
    ALTER COLUMN question_text TYPE TEXT;
```

#### ⑤ DB 인덱스 추가
- **파일:** `src/main/resources/db/migration/V3__add_indexes.sql` (신규 생성)
```sql
CREATE INDEX idx_exam_question_exam_id ON exam_question(exam_id);
CREATE INDEX idx_exam_subject_id ON exam(subject_id);
CREATE INDEX idx_subject_department_id ON subject(department_id);
```

#### ⑥ CSP 활성화
- **파일:** `src/main/java/knou/cbt/config/SecurityConfig.java`
- 주석 처리된 `contentSecurityPolicy` 블록 주석 해제
- 외부 CDN 확인 후 도메인 추가 (cdn.jsdelivr.net, uicdn.toast.com)

#### ⑦ ExcelParser 에러 메시지 개선
- **파일:** `src/main/java/knou/cbt/common/excel/ExcelParser.java`
- catch 블록에 `rowNum` 포함한 에러 메시지 추가
```java
} catch (Exception e) {
    throw new RuntimeException("엑셀 파싱 실패 (행: " + rowNum + ")", e);
}
```

---

## Phase 1 — 인프라 · 운영 기반 (1주, 초기 $47)

### 1-1. 도메인 구매
- **추천:** 가비아(gabia.com) — 한국어 인터페이스, .com 약 $15/년, .kr 약 $10/년
- **대안:** Cloudflare Registrar — 최저가($8~10/년), 영문 인터페이스
- 구매 후 Render 대시보드의 Custom Domain에 연결

### 1-2. Render Starter 플랜 업그레이드 ($7/월)
- 콜드 스타트 완전 제거, 24시간 상시 운영
- Render 대시보드 → Instance Type → Starter 선택

### 1-3. Supabase Pro 업그레이드 ($25/월)
- DB 커넥션 500개, 자동 일일 백업 포함
- **중요:** Transaction pooler URL 사용 (Direct Connection 아님)
  - Supabase 대시보드 → Connect → Transaction Mode URL 복사
  - `application-prod.yml`의 `SPRING_DATASOURCE_URL` 환경변수를 이 URL로 변경

### 1-4. Sentry 에러 모니터링 연동 (무료)
- sentry.io 가입 → Java/Spring Boot 프로젝트 생성
- `build.gradle`에 의존성 추가:
```groovy
implementation 'io.sentry:sentry-spring-boot-starter-jakarta:7.14.0'
```
- `application-prod.yml`에 DSN 추가:
```yaml
sentry:
  dsn: ${SENTRY_DSN}
  traces-sample-rate: 0.1
```

### 1-5. robots.txt + sitemap.xml + SEO 메타태그
- `src/main/resources/static/robots.txt` 생성
- `src/main/resources/static/sitemap.xml` 생성 (주요 URL 포함)
- `layout.html`에 OG 태그, description 메타태그 추가

### 1-6. 개인정보처리방침 페이지 추가
현재 회원가입 없어도 서버 로그(IP), 세션 쿠키를 수집하므로 최소한 필요.
나중에 회원가입 추가 시 이메일 항목만 추가하면 됨.
- `src/main/resources/templates/privacy.html` 생성
- SecurityConfig에 `/privacy` 퍼블릭 허용 추가
- **최소 포함 내용:** 수집 항목(서버 로그, 세션), 목적, 보유 기간, 문의처

### 1-7. Render 헬스체크 설정 확인
- Render 대시보드 → Health Check Path: `/health` 설정
- 이미 `HealthCheckController`가 있으므로 설정만 하면 됨

---

## Phase 2 — 기출 데이터 자동화 (2~3주, Claude API $5~20)

### 2-1. PDF/이미지 자동 추출 Python 스크립트

**흐름:**
```
PDF/HWP → (HWP는 PDF로 변환 선행) → Claude API (vision) → JSON → DB 직접 INSERT
```

**스크립트 구성:**
- `extractor/extract_exam.py` — Claude API로 PDF 분석, JSON 출력
- `extractor/upload_to_db.py` — 추출된 JSON을 DB에 INSERT
- `extractor/upload_images.py` — 이미지를 Supabase Storage에 업로드

**이미지 처리 전략:**
- Claude API가 PDF에서 이미지 영역을 인식 → base64로 추출
- Supabase Storage `exam` 버킷에 자동 업로드 → URL 반환
- `exam_question.image_url`에 저장

**예상 비용 (중간 규모 기준: 20개 과목 × 7년 × 평균 25문제):**
- 총 처리 분량: PDF 약 140개
- Claude Haiku 사용 시: 약 $5~15 (일회성)

---

## Phase 3 — 핵심 기능 추가 (1~2개월)

### 3-1. 학생 회원가입/로그인
- 기존 `users` 테이블에 `ROLE_USER` 추가 (현재는 `ROLE_ADMIN`만 있음)
- 회원가입 폼, 이메일 인증 (선택), 소셜 로그인 (선택)
- 마이페이지 기본 구성

### 3-2. 오답 노트
- `exam_wrong_note` 테이블 신규 생성
- 결과 페이지에서 틀린 문제 자동 저장
- 마이페이지에서 오답 모아 다시 풀기

### 3-3. 학습 이력
- `exam_history` 테이블: user_id, exam_id, score, created_at
- 마이페이지에서 시험별 점수 추이 확인

### 3-4. Google AdSense 적용
- 조건: 독자 도메인 + 개인정보처리방침 + 콘텐츠 충분 + 3개월 이상 운영
- `layout.html`에 AdSense 스크립트 삽입 (코드 한 줄)
- **예상 월 수입:** 트래픽 증가 전 $5~30, 안정 후 인프라 비용 일부 충당 가능

---

## Phase 4 — 운영 도구 (2~3개월 이후)

### 4-1. 관리자 대시보드
- 일 방문자 수, 인기 시험 TOP 5, 최근 에러 로그

### 4-2. 랜덤 모의고사
- 여러 연도 문제를 섞어서 출제하는 기능

---

## 전체 타임라인 및 비용 요약

| Phase | 기간 | 비용 | 주요 작업 |
|-------|------|------|-----------|
| Phase 0 | 1~3일 | $0 | 버그 수정 3개 + 환경 분리 |
| Phase 1 | 1주 | $47 (초기) + $33/월 | 인프라 세팅, Sentry, SEO, 개인정보처리방침 |
| Phase 2 | 2~3주 | $5~20 (일회성) | PDF 자동화 스크립트, 기출 데이터 입력 |
| Phase 3 | 1~2개월 | $33/월 | 회원 기능, 오답노트, AdSense |
| Phase 4 | 이후 | $33/월 | 관리자 대시보드, 랜덤 모의고사 |

**고정 인프라 비용:**
- Render Starter: $7/월
- Supabase Pro: $25/월  
- 도메인: $1.2/월 (연간 $15)
- **합계: 약 $33/월 (≈ 47,000원/월)**

**Claude Pro:** 집중 개발 기간에만 $20/월. 유지보수 단계에서는 해지 가능.

---

## 작업 체크리스트

### Phase 0 (바로 시작)
- [x] ① GlobalExceptionHandler 로깅 추가
- [x] ② 세션키 examId 포함으로 수정
- [x] ③ answers null 체크 추가
- [x] application-local.yml 신규 생성
- [x] ④ V2__alter_question_text.sql 생성
- [x] ⑤ V3__add_indexes.sql 생성
- [x] ⑥ CSP 주석 해제
- [x] ⑦ ExcelParser 에러 메시지 개선

### Phase 1 (1주 내)
- [ ] 도메인 구매 (가비아 또는 Cloudflare)
- [ ] Render Starter 업그레이드
- [ ] Supabase Pro 업그레이드 + Transaction pooler URL 변경
- [x] Sentry 연동 (코드 완료 — Render에 SENTRY_DSN 환경변수 추가 필요)
- [x] robots.txt, sitemap.xml 추가
- [x] 개인정보처리방침 페이지
- [ ] Render 헬스체크 경로 설정 (/health)

### Phase 2
- [ ] Python 추출 스크립트 작성 (Claude API)
- [ ] 이미지 자동 업로드 구현
- [ ] 기출 데이터 전체 입력

### Phase 3
- [ ] 회원가입/로그인 기능
- [ ] 오답 노트
- [ ] 학습 이력
- [ ] AdSense 신청 및 적용

### Phase 4
- [ ] 관리자 대시보드
- [ ] 랜덤 모의고사
