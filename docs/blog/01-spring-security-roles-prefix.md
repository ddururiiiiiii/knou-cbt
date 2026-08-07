# Spring Security의 `.roles()`는 이미 `ROLE_`을 붙여준다

## 문제

로컬에서 관리자 계정을 새로 만들고 로그인 테스트를 하는데, 분명 비밀번호도 맞게 넣었는데 계속 로그인에 실패하거나, 로그인은 되는데 `/admin/**` 화면에 들어가면 403이 떴다. 로그도 딱히 에러를 뱉지 않아서 처음엔 비밀번호 해시가 잘못됐나 의심했다.

DB를 열어보니 `role` 컬럼에 이렇게 들어가 있었다.

```sql
role = 'ROLE_ADMIN'
```

"관리자니까 ROLE_ADMIN이 맞겠지" — 당연해 보이는 값이었다. 그런데 이게 범인이었다.

## 원인

이 프로젝트는 `UserDetailsService` 구현체에서 사용자 권한을 이렇게 만든다.

```java
// CustomUserDetailsService.java
return org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail())
        .password(user.getPassword())
        .roles(user.getRole().name()) // DB에는 "ADMIN" 저장 → Security가 ROLE_ 붙여줌
        .build();
```

`UserDetails.Builder.roles(String...)`는 넘겨준 문자열 앞에 **자동으로 `ROLE_`을 붙여서** `GrantedAuthority`를 만든다. 이건 Spring Security의 오래된 관례로, `hasRole("ADMIN")`이나 `.hasRole("ADMIN")` 같은 API들도 마찬가지로 내부적으로 `ROLE_`을 자동으로 붙이는 걸 전제로 설계돼 있다.

즉 DB에 `ADMIN`만 저장해야 Security가 `ROLE_ADMIN`으로 인식한다. 그런데 DB에 이미 `ROLE_ADMIN`이라고 넣어버리면:

```
DB 값: "ROLE_ADMIN"
.roles("ROLE_ADMIN") 호출
→ 내부적으로 "ROLE_" + "ROLE_ADMIN" = "ROLE_ROLE_ADMIN"
```

`ROLE_ROLE_ADMIN`이라는, 어디에도 매칭되지 않는 권한이 만들어진다. `SecurityConfig`에서 `.requestMatchers("/admin/**").hasRole("ADMIN")`은 `ROLE_ADMIN`을 찾는데 실제 권한은 `ROLE_ROLE_ADMIN`이니 당연히 매칭되지 않아 403이 난다. (참고로 `.roles()`에 `ROLE_` 접두사가 이미 붙은 문자열을 넣으면 Spring Security가 `IllegalArgumentException`을 던지도록 막아주는 경우도 있는데, 버전/구성에 따라 조용히 통과되는 경우도 있어서 더 헷갈리기 쉽다.)

## 해결

간단하다. DB의 `role` 컬럼엔 접두사 없이 값만 저장한다.

```sql
INSERT INTO users (email, password, role)
VALUES ('admin@example.com', '$2a$10$...(bcrypt 해시)...', 'ADMIN');
```

| DB `role` 값 | Security가 실제로 인식하는 권한 | 결과 |
|---|---|---|
| `ADMIN` | `ROLE_ADMIN` | 정상 |
| `ROLE_ADMIN` | `ROLE_ROLE_ADMIN` | 403 (권한 없음 취급) |

## 교훈

- `.roles()`라는 이름만 보면 "역할 이름을 그대로 넣는 곳"처럼 보이지만, 실제로는 "`ROLE_` 없는 짧은 이름을 넣는 곳"이다. API 이름과 실제 계약이 미묘하게 다른 전형적인 사례.
- 이런 종류의 버그는 에러 로그가 친절하지 않다. "권한이 없다"는 게 "권한 자체가 이상한 이름으로 만들어졌다"는 뜻일 수도 있다는 걸 의심하려면, 실제로 `Authentication` 객체의 `getAuthorities()`를 찍어보는 게 제일 빠르다.
- 이후로 계정을 새로 만들 때마다 이 함정을 다시 밟지 않으려고, 로컬 개발 가이드 문서에 "DB엔 `ADMIN`만, `ROLE_ADMIN` 금지"를 못 박아뒀다.
