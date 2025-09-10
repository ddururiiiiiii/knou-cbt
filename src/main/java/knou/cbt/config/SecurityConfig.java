package knou.cbt.config;

import knou.cbt.global.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // ← API는 CSRF 체크 제외
                )
                .headers(headers -> headers
                        // 캐시 비활성화 (민감 페이지 캐싱 방지)
                        .cacheControl(cache -> cache.disable())
                        // XSS 대응: Content Security Policy 적용
                        // → script는 오직 현재 서버('self')에서만 로드 가능
//                        .contentSecurityPolicy(csp ->
//                                csp.policyDirectives(
//                                        "default-src 'self'; " +
//                                        "script-src 'self' uicdn.toast.com cdn.jsdelivr.net; " +
//                                        "style-src 'self' cdn.jsdelivr.net uicdn.toast.com 'unsafe-inline'; " +
//                                        "font-src 'self' cdn.jsdelivr.net uicdn.toast.com; " +
//                                        "img-src 'self' data:;"
//                                )
//                        )
                )
                .authorizeHttpRequests(auth -> auth
                        // 누구나 접근 가능
                        .requestMatchers("/", "/exams/**", "/requests/**", "/notices/**", "/api/**",
                                "/css/**", "/js/**", "/error/**", "/uploads/**").permitAll()
                        // 로그인 화면은 누구나 접근 가능
                        .requestMatchers("/admin/login", "/login").permitAll()
                        // 관리자 전용
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/notices/new", "/notices/*/edit", "/notices/*/delete").hasRole("ADMIN")
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
                .userDetailsService(customUserDetailsService);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
