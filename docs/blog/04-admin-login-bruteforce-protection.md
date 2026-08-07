# 관리자 로그인 브루트포스 방지 구현기

## 문제

1인 개발로 운영하는 서비스라 관리자 계정은 딱 하나뿐이다. 그런데 로그인 화면은 인터넷에 그대로 노출돼 있고, 별도의 WAF나 리버스 프록시 레벨 방화벽도 없다. 누군가 비밀번호를 무작정 대입해보려고 마음먹으면 막을 방법이 아무것도 없는 상태였다.

거창한 솔루션(Redis 기반 rate limiter, 외부 WAF 서비스 등)을 붙이기엔 트래픽도 적고 운영 인력도 나 혼자다. "필요한 만큼만, 최대한 단순하게" 막는 방법이 필요했다.

## 설계

요구사항을 최소로 좁혔다.

- 같은 곳에서 반복적으로 로그인을 실패하면 잠깐 막는다
- 별도 인프라(Redis, DB 테이블) 없이, 애플리케이션 메모리만으로 충분하다 (관리자 계정이 하나뿐이라 트래픽/데이터량이 크지 않음)
- 잠금 기준은 **IP 단위**로 한다 — 계정이 하나뿐이니 "계정 잠금"은 의미가 없다. 계정을 잠그면 본인도 못 들어가고, 공격자는 어차피 그 계정 하나만 노릴 것이다.

`ConcurrentHashMap`으로 IP별 실패 횟수와 잠금 시각을 들고 있는 서비스 하나면 충분했다.

```java
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private record Attempt(int count, Instant lastAttemptAt, Instant lockedUntil) {}

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    public void loginFailed(String key) {
        attempts.compute(key, (k, prev) -> {
            Instant now = Instant.now();
            boolean expired = prev != null
                    && Duration.between(prev.lastAttemptAt(), now).compareTo(LOCK_DURATION) > 0;
            int count = (prev == null || expired) ? 1 : prev.count() + 1;
            Instant lockedUntil = count >= MAX_ATTEMPTS ? now.plus(LOCK_DURATION) : null;
            return new Attempt(count, now, lockedUntil);
        });
    }

    public void loginSucceeded(String key) {
        attempts.remove(key);
    }

    public boolean isBlocked(String key) {
        Attempt attempt = attempts.get(key);
        if (attempt == null || attempt.lockedUntil() == null) return false;
        if (Instant.now().isAfter(attempt.lockedUntil())) {
            attempts.remove(key);
            return false;
        }
        return true;
    }
}
```

IP당 5회 연속 실패하면 15분 잠금. 마지막 실패로부터 15분이 지나면 카운트가 자동으로 리셋된다.

## 연결하기 — 필터와 이벤트 리스너

`isBlocked()`를 로그인 요청보다 먼저 체크해야 하니 `OncePerRequestFilter`로 걸었다.

```java
public class LoginAttemptFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttemptService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(request.getMethod()) && "/login".equals(request.getRequestURI())) {
            String ip = ClientIpUtils.getClientIp(request);
            if (loginAttemptService.isBlocked(ip)) {
                response.sendRedirect("/admin/login?error=locked");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
```

실패/성공 카운트는 Spring Security가 자체적으로 발행하는 인증 이벤트를 그대로 구독해서 처리했다. 컨트롤러나 필터에 로직을 섞지 않아도 된다.

```java
@Component
public class LoginAttemptEventListener {

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        currentClientIp().ifPresent(loginAttemptService::loginFailed);
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        currentClientIp().ifPresent(loginAttemptService::loginSucceeded);
    }
}
```

## 함정 — Render는 프록시 뒤에 있다

배포 환경(Render)에서는 요청이 리버스 프록시를 한 번 거쳐서 들어온다. 이 상태에서 `request.getRemoteAddr()`를 그대로 쓰면 **실제 클라이언트 IP가 아니라 프록시의 IP**가 잡힌다. 그러면 전 세계 모든 사용자의 로그인 실패가 전부 "같은 IP"로 집계돼서, 아무 관계 없는 사용자가 다른 사람의 실패 때문에 같이 잠기는 사고가 날 수 있다.

그래서 `X-Forwarded-For` 헤더를 우선적으로 확인하도록 별도 유틸을 뒀다.

```java
public final class ClientIpUtils {
    // Render 등 프록시 뒤에서는 remoteAddr가 프록시 IP로 잡히므로 X-Forwarded-For를 우선 확인
    public static String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

## 알아두고 쓰는 트레이드오프

이 구현에는 의도적으로 받아들인 한계가 몇 가지 있다.

- **메모리 저장이라 서버 재시작하면 모든 잠금이 풀린다.** 반대로 얘기하면, 관리자 본인이 실수로 잠겼을 때 "15분 기다리기" 대신 "서버 재시작"이라는 비상 탈출구가 생긴 셈이다. 인스턴스가 하나뿐인 소규모 서비스라 가능한 트레이드오프다.
- **IP 단위 잠금이라, 같은 공용 IP(사내망, 카페 와이파이 등)를 쓰는 다른 사람이 내 실패 때문에 같이 잠길 수 있다.** 반대로 관리자가 와이파이에서 테더링으로 전환하면 잠금이 그냥 풀려버린다. 완벽한 방어는 아니지만, "무작정 대입 공격을 몇 초 만에 끝내지 못하게 만드는" 목적은 충분히 달성한다.
- 인스턴스를 여러 대로 늘리는 순간 이 방식은 무너진다(각 인스턴스가 자기 메모리만 봄). 지금은 인스턴스가 하나뿐이라 문제없지만, 나중에 스케일아웃하게 되면 Redis 등 공유 저장소로 옮겨야 한다.

## 교훈

- 완벽한 보안보다 "지금 규모에 맞는 충분한 방어"를 먼저 놓는 게 1인 개발에서는 현실적이다. 계정이 하나뿐인 서비스에 계정 잠금 정책을 고민하는 건 시간 낭비였고, IP 기반이 오히려 더 맞는 선택이었다.
- 배포 환경이 프록시를 거친다는 사실은 코드를 짤 때 항상 의식해야 한다. 로컬에서는 완벽하게 동작하는 IP 기반 로직이 배포하고 나서야 조용히 무너지는 전형적인 케이스였다.
