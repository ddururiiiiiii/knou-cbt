package knou.cbt.global.security.oauth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 로그인 성공 시 "최근에 이 소셜로 로그인했다"를 클라이언트 쿠키에 남겨서,
 * 다음에 /login 페이지를 열었을 때 해당 버튼을 강조해줄 수 있게 한다.
 */
@Component
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final String COOKIE_NAME = "last_login_provider";
    private static final int COOKIE_MAX_AGE_SECONDS = 365 * 24 * 60 * 60;

    public OAuthLoginSuccessHandler() {
        setDefaultTargetUrl("/mypage");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, jakarta.servlet.ServletException {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2Token) {
            Cookie cookie = new Cookie(COOKIE_NAME, oAuth2Token.getAuthorizedClientRegistrationId());
            cookie.setPath("/");
            cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
            response.addCookie(cookie);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
