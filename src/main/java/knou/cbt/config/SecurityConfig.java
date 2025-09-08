package knou.cbt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .cacheControl(cache -> cache.disable())
                ) //캐시 방지 필터
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/exams/**", "/requests/**", "/notices/**", "/api/**",
                                "/css/**", "/js/**", "/error/**").permitAll()
                        .requestMatchers("/admin/login", "/login").permitAll()   // 로그인은 열어줌
                        .requestMatchers("/admin/**").hasRole("ADMIN")           // 관리자 전용
                        .requestMatchers("/notices/new", "/notices/*/edit", "/notices/*/delete").hasRole("ADMIN")
                        .anyRequest().authenticated()                           // 그 외는 인증 필요
                )
                .formLogin(login -> login
                        .loginPage("/admin/login")   // GET: 로그인 폼
                        .loginProcessingUrl("/login") // POST: 로그인 처리 (기본값 그대로 둠)
                        .defaultSuccessUrl("/admin/departments", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("1234")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
