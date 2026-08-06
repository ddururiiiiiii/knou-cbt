package knou.cbt.global.maintenance;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knou.cbt.domain.siteoperation.exception.MaintenanceModeException;
import knou.cbt.domain.siteoperation.model.SiteOperationSetting;
import knou.cbt.domain.siteoperation.service.SiteOperationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class MaintenanceInterceptor implements HandlerInterceptor {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // 응시 중이던 사용자의 최종 제출만은 점검 중에도 통과시킨다.
    // (답안이 서버/DB에 저장되지 않고 브라우저에만 있으므로, 여기서 막으면 답안이 그대로 소실됨)
    private static final String EXAM_SUBMIT_PATTERN = "/exams/*/solve";

    private final SiteOperationSettingService settingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        SiteOperationSetting setting = settingService.get();

        if (!Boolean.TRUE.equals(setting.getMaintenanceEnabled())) {
            return true;
        }

        if (isAdmin() || isExamFinalSubmit(request)) {
            return true;
        }

        throw new MaintenanceModeException(setting.getMaintenanceMessage(), setting.getMaintenanceExpectedEndAt());
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

    private boolean isExamFinalSubmit(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && PATH_MATCHER.match(EXAM_SUBMIT_PATTERN, request.getRequestURI());
    }
}
