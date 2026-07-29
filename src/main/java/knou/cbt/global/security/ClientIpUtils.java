package knou.cbt.global.security;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientIpUtils {

    private ClientIpUtils() {
    }

    // Render 등 프록시 뒤에서는 remoteAddr가 프록시 IP로 잡히므로 X-Forwarded-For를 우선 확인
    public static String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}