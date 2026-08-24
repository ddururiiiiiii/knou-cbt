package knou.cbt.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Spring Security 6부터 CSRF 토큰이 지연 로딩(DeferredCsrfToken) 방식으로 바뀌어서,
 * 세션이 없는 방문자가 큰 페이지(문제 풀기 화면)에 처음 접속하면 응답 버퍼가 다 차서
 * 커밋된 뒤에야(= HTML의 &lt;form&gt; 태그를 렌더링하는 시점에야) 토큰을 생성/세션에 저장하려다
 * "Cannot create a session after the response has been committed" 500 에러가 났다.
 * 요청 처리 맨 앞(CsrfFilter 직후, 아직 응답에 한 바이트도 쓰기 전)에서 토큰을 미리 한 번
 * 읽어와 세션 생성 및 저장을 끝내둠으로써 문제를 막는다.
 *
 * 문제 풀기 화면(GET /exams/{id}/solve)에만 좁혀서 적용한다 — 모든 요청에 걸면 폼이 없는
 * 페이지(목록/공지 등)를 스치는 봇·비로그인 방문자에게도 매번 세션이 생겨, 크롤러 트래픽으로
 * 서버 메모리 한도를 넘겼던 과거 이슈를 다시 키울 수 있다.
 */
public class CsrfTokenEagerLoadingFilter extends OncePerRequestFilter {

    private static final Pattern EXAM_SOLVE_PATH = Pattern.compile("^/exams/\\d+/solve$");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("GET".equalsIgnoreCase(request.getMethod()) && EXAM_SOLVE_PATH.matcher(request.getRequestURI()).matches()) {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                csrfToken.getToken();
            }
        }
        filterChain.doFilter(request, response);
    }
}
