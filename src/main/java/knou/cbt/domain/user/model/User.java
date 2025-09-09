package knou.cbt.domain.user.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    private Long id;
    private String email;
    private String password;
    private Role role;  // enum 타입
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 정적 팩토리 메서드
    public static User create(String email, String encodedPassword, Role role) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.role = role;
        return user;
    }
}
