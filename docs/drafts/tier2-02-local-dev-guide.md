# 로컬 개발환경 구성 가이드 (내부용)

## 개요
- 목적: 새로 개발 참여하거나, 오랜만에 로컬에서 작업할 때 처음부터 헤매지 않기 위한 가이드
- 핵심 3단계: ① 로컬 Postgres 띄우기 → ② `local` 프로필로 앱 실행 → ③ 관리자 계정 직접 시드하기
- 저장소에 `docker-compose.yml`은 따로 없음 → Postgres는 `docker run` 명령으로 즉석에서 띄우는 방식

---

## 1. 로컬 Postgres 띄우기

`application-local.yml` 기준으로 아래 값에 맞춰 띄우면 됨.

| 항목 | 값 |
|---|---|
| DB 이름 | `knou_cbt_local` |
| 계정 | `postgres` |
| 비밀번호 | `postgres` |
| 포트 | `5432` |

```bash
docker run --name knou-cbt-local-db \
  -e POSTGRES_DB=knou_cbt_local \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:16
```

- 다음에 또 쓸 땐 `docker start knou-cbt-local-db`로 재시작하면 됨 (매번 새로 만들 필요 없음)
- 완전히 초기화하고 싶으면 `docker rm -f knou-cbt-local-db` 후 위 명령 다시 실행

---

## 2. `local` 프로필로 앱 실행

- `local` 프로필은 Flyway가 켜져 있어서(`flyway.enabled: true`) 앱 실행 시 `V1~V8` 마이그레이션이 로컬 DB에 자동으로 순서대로 적용됨 → 테이블을 직접 만들 필요 없음
- IntelliJ 기준: Run Configuration의 VM options 또는 Active profiles에 `local` 지정
- 커맨드라인 기준:
  ```bash
  ./gradlew bootRun --args='--spring.profiles.active=local'
  ```
- Supabase 관련 값(`supabase.url`, `supabase.key`)은 로컬용 더미 값(`http://localhost:54321`, `local-dev-key`)으로 채워져 있음 → **로컬에서는 이미지 업로드 기능이 실제로는 동작 안 함**. 이미지 업로드까지 테스트하려면 별도로 운영/개발용 Supabase 키를 임시로 넣어야 함 (커밋 금지, 로컬에서만 값 바꿔서 테스트 후 원복)

---

## 3. 관리자 계정 시드 (여기가 제일 함정 많음)

앱에 회원가입 화면이 없어서, 로컬에서도 관리자 계정을 **DB에 직접 넣어야** 로그인 테스트 가능함.

### `users` 테이블 구조 (`V1__init.sql` 기준)

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 함정 ① — 비밀번호를 평문으로 넣으면 로그인 절대 안 됨

`password` 컬럼엔 **BCrypt로 해시된 값**이 들어가야 함. Spring Security가 로그인 시 입력값을 같은 방식으로 해시해서 비교하기 때문에, 평문을 그냥 넣으면 100% 로그인 실패함.

로컬에서 해시값 만드는 법 — 아래 코드를 테스트 클래스나 임시 `main()`에 잠깐 넣어서 실행:
```java
System.out.println(
    new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("test1234")
);
```

### 함정 ② — `role` 컬럼에 `ROLE_ADMIN`을 넣으면 오히려 인식 안 됨

```java
// CustomUserDetailsService.java
.roles(user.getRole().name()) // DB에는 "ADMIN" 저장 → Security가 ROLE_ 붙여줌
```

Spring Security의 `.roles()`는 넣어준 값 앞에 자동으로 `ROLE_`을 붙여줌. 그래서 DB엔 반드시 **`ADMIN`만** 저장해야 함. `ROLE_ADMIN`을 넣으면 Security가 `ROLE_ROLE_ADMIN`으로 인식해버려서 관리자 권한이 전혀 안 먹힘.

| DB `role` 값 | Security가 인식하는 권한 | 결과 |
|---|---|---|
| `ADMIN` | `ROLE_ADMIN` | ✅ 정상 |
| `ROLE_ADMIN` | `ROLE_ROLE_ADMIN` | ❌ 관리자 권한 없음 취급 |

### 최종 시드 SQL 예시

```sql
INSERT INTO users (email, password, role)
VALUES (
  'admin@local.test',
  '$2a$10$....(위에서 생성한 bcrypt 해시)....',
  'ADMIN'
);
```

---

## 4. 그 외 흔히 겪는 함정

- 로컬 DB를 껐다 켰다 하다가 마이그레이션이 중간에 꼬이면, 컨테이너를 통째로 지우고(`docker rm -f`) 처음부터 다시 띄우는 게 제일 빠름 (로컬이라 데이터 손실 걱정 없음)
- 로컬에서 이미지 업로드 관련 화면 작업할 땐 Supabase 더미 키로는 실제 업로드가 안 된다는 걸 잊고 "왜 안 되지" 하고 헤맬 수 있음 → 2번 항목 참고
