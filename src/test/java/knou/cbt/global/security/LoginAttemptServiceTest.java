package knou.cbt.global.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAttemptServiceTest {

    @Test
    void 다섯번_실패하면_잠긴다() {
        LoginAttemptService service = new LoginAttemptService();
        String ip = "1.2.3.4";

        for (int i = 0; i < 4; i++) {
            service.loginFailed(ip);
        }
        assertThat(service.isBlocked(ip)).isFalse();

        service.loginFailed(ip);
        assertThat(service.isBlocked(ip)).isTrue();
    }

    @Test
    void 로그인_성공하면_기록이_초기화된다() {
        LoginAttemptService service = new LoginAttemptService();
        String ip = "1.2.3.4";

        for (int i = 0; i < 5; i++) {
            service.loginFailed(ip);
        }
        assertThat(service.isBlocked(ip)).isTrue();

        service.loginSucceeded(ip);
        assertThat(service.isBlocked(ip)).isFalse();
    }

    @Test
    void 다른_IP는_서로_영향을_주지_않는다() {
        LoginAttemptService service = new LoginAttemptService();

        for (int i = 0; i < 5; i++) {
            service.loginFailed("1.1.1.1");
        }

        assertThat(service.isBlocked("1.1.1.1")).isTrue();
        assertThat(service.isBlocked("2.2.2.2")).isFalse();
    }
}