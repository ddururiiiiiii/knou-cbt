package knou.cbt.config;

import knou.cbt.global.security.CsrfTokenEagerLoadingFilter;
import knou.cbt.global.security.CustomUserDetailsService;
import knou.cbt.global.security.LoginAttemptFilter;
import knou.cbt.global.security.LoginAttemptService;
import knou.cbt.global.security.oauth.CustomOAuth2UserService;
import knou.cbt.global.security.oauth.OAuthLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final LoginAttemptService loginAttemptService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;

    @Value("${remember-me.key:knou-cbt-remember-key}")
    private String rememberMeKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new LoginAttemptFilter(loginAttemptService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new CsrfTokenEagerLoadingFilter(), CsrfFilter.class)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // ← API는 CSRF 체크 제외
                )
                .headers(headers -> headers
                        // 캐시 비활성화 (민감 페이지 캐싱 방지)
                        // 주의: HeadersConfigurer의 cacheControl()은 기본적으로 Cache-Control: no-cache 헤더를
                        // 자동으로 붙여주는 기능이며, .disable()을 호출하면 그 헤더 자동 삽입 자체를 꺼버려서
                        // 오히려 브라우저 캐싱이 허용돼버린다 (여기서 브라우저가 옛날 페이지를 계속 보여주던 원인).
                        // 그래서 별도 설정 없이 기본 동작(자동으로 no-cache 헤더 추가)을 그대로 사용한다.
                        // XSS 대응: Content Security Policy 적용
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives(
                                        "default-src 'self'; " +
                                        "script-src 'self' uicdn.toast.com cdn.jsdelivr.net www.googletagmanager.com " +
                                                "pagead2.googlesyndication.com googleads.g.doubleclick.net tpc.googlesyndication.com " +
                                                "*.adtrafficquality.google; " +
                                        "style-src 'self' cdn.jsdelivr.net uicdn.toast.com 'unsafe-inline'; " +
                                        "font-src 'self' cdn.jsdelivr.net uicdn.toast.com; " +
                                        "img-src 'self' data: blob: www.google-analytics.com https://*.supabase.co " +
                                                "pagead2.googlesyndication.com googleads.g.doubleclick.net *.google.com *.gstatic.com " +
                                                "*.adtrafficquality.google; " +
                                        "connect-src 'self' www.google-analytics.com pagead2.googlesyndication.com googleads.g.doubleclick.net " +
                                                "*.adtrafficquality.google; " +
                                        "frame-src googleads.g.doubleclick.net tpc.googlesyndication.com *.google.com " +
                                                "*.adtrafficquality.google;"
                                )
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        // 누구나 접근 가능
                        .requestMatchers("/", "/exams/**", "/requests/**", "/notices/**", "/api/**",
                                "/css/**", "/js/**", "/error/**", "/uploads/**", "/privacy", "/about", "/faq",
                                "/robots.txt", "/sitemap.xml", "/ads.txt").permitAll()
                        // 관리자 로그인 화면은 누구나 접근 가능
                        .requestMatchers("/admin/login").permitAll()
                        // 회원(소셜) 로그인 화면과 OAuth2 인가/콜백 경로도 누구나 접근 가능
                        // (네비게이션에는 feature flag로 숨겨져 있어도 URL을 직접 아는 사람은 테스트 가능해야 함)
                        .requestMatchers("/login", "/oauth2/**", "/login/oauth2/**").permitAll()
                        // 관리자 전용
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 마이페이지는 로그인한 회원만 (관리자 세션은 ROLE_ADMIN이라 ROLE_USER가 아님 ->
                        // authenticated()만 걸면 관리자가 접근했을 때 MemberPrincipal이 아니라서 500이 남)
                        .requestMatchers("/mypage/**").hasRole("USER")
                        // 공지사항의 등록/수정/삭제는 URL 패턴이 조회와 겹쳐서(둘 다 /notices/**)
                        // 여기서 걸러낼 수 없음 -> @EnableMethodSecurity + 컨트롤러의 @PreAuthorize로 강제함
                        .requestMatchers("/health").permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                // formLogin/oauth2Login을 동시에 쓰면 어느 쪽 로그인 페이지가 기본 진입점이 될지가
                // 내부 등록 순서에 좌우돼 불확실하므로, 두 경우 모두 명시적으로 매처를 지정해서 고정한다.
                // (실제로 formLogin이 명시 없이도 기본값을 선점해서 /mypage 같은 회원 전용 경로가
                // /admin/login으로 새는 문제가 있었음 -> 아래처럼 명시해서 해결)
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/admin/login"),
                                new AntPathRequestMatcher("/admin/**")
                        )
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new AntPathRequestMatcher("/**")
                        )
                )
                // 관리자 로그인/로그아웃 설정 (ID/PW)
                // loginProcessingUrl은 그대로 "/login"(POST) 유지 — 폼로그인 필터가 DispatcherServlet보다
                // 먼저 가로채는 요청이라, 회원용 GET "/login" 페이지(MemberLoginController)와 충돌하지 않는다.
                .formLogin(login -> login
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/exams", true)
                        .permitAll()
                )
                // 회원 로그인 설정 (구글/카카오/네이버 소셜 로그인)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuthLoginSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key(rememberMeKey)
                        .tokenValiditySeconds(14 * 24 * 60 * 60) // 14일
                        .userDetailsService(customUserDetailsService)
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
