package knou.cbt.global.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class LoginAttemptEventListener {

    private final LoginAttemptService loginAttemptService;

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        currentClientIp().ifPresent(loginAttemptService::loginFailed);
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        currentClientIp().ifPresent(loginAttemptService::loginSucceeded);
    }

    private java.util.Optional<String> currentClientIp() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return java.util.Optional.empty();
        }
        HttpServletRequest request = attrs.getRequest();
        return java.util.Optional.of(ClientIpUtils.getClientIp(request));
    }
}