# `robots.txt`/`sitemap.xml`이 로그인 화면으로 리다이렉트되던 버그 (내부용)

## 개요
- 애드센스 사이트 소유권 인증을 `ads.txt` 방식으로 진행하던 중, 실제로 `/ads.txt`가 브라우저/크롤러에게 정상적으로 보이는지 확인차 curl로 찍어봄
- `robots.txt`/`sitemap.xml`도 같이 확인해봤더니, 셋 다 **비로그인 상태에서 `/admin/login`으로 302 리다이렉트**되고 있었음
- 즉 지금까지 검색엔진 크롤러가 이 파일들을 정상적으로 읽은 적이 없었을 가능성이 높고, `ads.txt`도 이대로면 구글 크롤러 인증에 실패했을 것
- 커밋 `f32cdbd`로 수정

---

## 1. 무슨 일이 있었나

`SecurityConfig`의 인증 규칙을 보면:

```java
.authorizeHttpRequests(auth -> auth
        // 누구나 접근 가능
        .requestMatchers("/", "/exams/**", "/requests/**", "/notices/**", "/api/**",
                "/css/**", "/js/**", "/error/**", "/uploads/**", "/privacy").permitAll()
        .requestMatchers("/admin/login", "/login").permitAll()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .requestMatchers("/health").permitAll()
        // 나머지는 인증 필요
        .anyRequest().authenticated()
)
```

`robots.txt`, `sitemap.xml`, 그리고 새로 추가하려던 `ads.txt`는 이 `permitAll` 목록 어디에도 없었음. 그래서 마지막 규칙(`anyRequest().authenticated()`)에 걸려서 **로그인 안 된 사용자는 전부 `/admin/login`으로 튕기고 있었음**.

```bash
curl -sI https://www.knou-cbt.com/robots.txt
# HTTP/2 302
# location: https://www.knou-cbt.com/admin/login
```

정적 파일이 `src/main/resources/static/` 밑에 있으면 Spring Boot가 자동으로 서빙은 해주지만, **서빙 이전에 Spring Security 필터 체인을 먼저 통과해야 한다**는 걸 놓친 사례. 파일이 존재하는지와, 그 경로에 실제로 접근 가능한지는 별개 문제임.

---

## 2. 왜 이제까지 발견이 안 됐나

- `robots.txt`/`sitemap.xml`은 Phase 1(2026-05-21)에 추가된 파일인데, 그 이후로 브라우저로 직접 열어서 확인해본 적이 없었음(자기 브라우저는 관리자로 로그인된 상태로 열어보면 정상 응답이라 문제를 못 느낌 — 관리자 세션이 있으면 `anyRequest().authenticated()`도 통과해버리기 때문에 로그인한 사람 눈에는 멀쩡해 보임)
- 검색엔진 크롤러나 애드센스 봇처럼 **비로그인 상태로 접근하는 주체 입장에서만 드러나는 문제**라서, 실제로 그런 크롤러가 오기 전까지는 증상이 안 보임
- 이번에 발견된 계기도 "그냥 파일이 있으니 되겠지"가 아니라, **애드센스 인증이 실패할까 봐 사전에 curl로 직접 확인**했기 때문 — 오픈 전 스모크 테스트에 "비로그인 상태로 정적 파일 접근" 항목이 없었다는 뜻이기도 함

---

## 3. 어떻게 고쳤나 (`f32cdbd`)

```java
.requestMatchers("/", "/exams/**", "/requests/**", "/notices/**", "/api/**",
        "/css/**", "/js/**", "/error/**", "/uploads/**", "/privacy",
        "/robots.txt", "/sitemap.xml", "/ads.txt").permitAll()   // 세 개 추가
```

`ads.txt`는 이 커밋에서 같이 신규 추가(`src/main/resources/static/ads.txt`)하면서 바로 `permitAll`에 포함시킴 — robots.txt/sitemap.xml처럼 나중에 따로 발견되는 걸 막기 위함.

수정 후 재확인:

```bash
curl -sI https://www.knou-cbt.com/robots.txt   # 200
curl -sI https://www.knou-cbt.com/sitemap.xml  # 200
curl -s  https://www.knou-cbt.com/ads.txt      # 정상 내용 출력
```

---

## 4. 교훈 / 재발 방지

- **"파일이 `static/` 밑에 있다" ≠ "누구나 접근 가능하다".** Spring Security를 쓰는 프로젝트에서는 정적 파일이라도 인증 규칙에 명시적으로 포함시켜야 함 — 특히 `robots.txt`/`sitemap.xml`/`ads.txt`/`.well-known/**`처럼 **비로그인 주체(크롤러, 외부 서비스 인증 봇)가 접근하는 파일**은 우선순위 높게 확인 대상.
- 관리자 세션으로 로그인한 채로 화면을 확인하는 습관은, "로그인 여부와 무관하게 공개되어야 하는 리소스"를 검증하는 데는 **함정**이 될 수 있음 — 로그인된 브라우저로 확인하면 이 버그는 절대 안 보임. 시크릿 창(비로그인 상태)이나 `curl` 같은 도구로 별도 확인이 필요.
- 새로운 외부 서비스(검색엔진, 광고 플랫폼, 소셜 로그인 등) 연동 작업을 할 때 "그 서비스가 실제로 우리 서버의 어떤 엔드포인트를 비로그인 상태로 호출하는가"를 먼저 따져보고, 그 목록을 인증 규칙과 대조해보는 절차가 있었다면 더 일찍 잡혔을 사례.
- 배포 전 스모크 테스트 체크리스트(Tier 1)에 "시크릿 창으로 `robots.txt`/`sitemap.xml`/`ads.txt` 접근 확인" 항목 추가를 고려할 것.
