package knou.cbt.config;

import knou.cbt.global.security.CustomUserDetailsService;
import knou.cbt.global.security.LoginAttemptFilter;
import knou.cbt.global.security.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final LoginAttemptService loginAttemptService;

    @Value("${remember-me.key:knou-cbt-remember-key}")
    private String rememberMeKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new LoginAttemptFilter(loginAttemptService), UsernamePasswordAuthenticationFilter.class)
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
                                "/css/**", "/js/**", "/error/**", "/uploads/**", "/privacy",
                                "/robots.txt", "/sitemap.xml", "/ads.txt").permitAll()
                        // 로그인 화면은 누구나 접근 가능
                        .requestMatchers("/admin/login", "/login").permitAll()
                        // 관리자 전용
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 공지사항의 등록/수정/삭제는 URL 패턴이 조회와 겹쳐서(둘 다 /notices/**)
                        // 여기서 걸러낼 수 없음 -> @EnableMethodSecurity + 컨트롤러의 @PreAuthorize로 강제함
                        .requestMatchers("/health").permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                // 로그인/로그아웃 설정
                .formLogin(login -> login
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/exams", true)
                        .permitAll()
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
