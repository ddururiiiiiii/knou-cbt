package knou.cbt.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginAttemptFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttemptService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(request.getMethod()) && "/login".equals(request.getRequestURI())) {
            String ip = ClientIpUtils.getClientIp(request);
            if (loginAttemptService.isBlocked(ip)) {
                response.sendRedirect("/admin/login?error=locked");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}