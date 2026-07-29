package knou.cbt.global.security;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private record Attempt(int count, Instant lastAttemptAt, Instant lockedUntil) {
    }

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    public void loginFailed(String key) {
        attempts.compute(key, (k, prev) -> {
            Instant now = Instant.now();
            // 마지막 실패로부터 잠금 시간이 지났으면 새로 카운트 시작
            boolean expired = prev != null && Duration.between(prev.lastAttemptAt(), now).compareTo(LOCK_DURATION) > 0;
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
        if (attempt == null || attempt.lockedUntil() == null) {
            return false;
        }
        if (Instant.now().isAfter(attempt.lockedUntil())) {
            attempts.remove(key);
            return false;
        }
        return true;
    }
}