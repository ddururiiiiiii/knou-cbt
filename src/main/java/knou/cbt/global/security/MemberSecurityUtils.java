package knou.cbt.global.security;

import knou.cbt.global.security.oauth.MemberPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class MemberSecurityUtils {

    private MemberSecurityUtils() {
    }

    /**
     * 현재 로그인한 회원의 id. 로그인 안 했거나 관리자 세션이면 null.
     */
    public static Long currentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof MemberPrincipal principal)) {
            return null;
        }
        return principal.getMemberId();
    }
}
