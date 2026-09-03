package knou.cbt.global.security.oauth;

import knou.cbt.domain.member.model.OAuthProvider;

import java.util.Map;

/**
 * 구글/카카오/네이버가 각각 다른 형태로 내려주는 사용자 정보를 (providerId, email, nickname)으로 통일한다.
 */
public record OAuthAttributes(String providerId, String email, String nickname) {

    @SuppressWarnings("unchecked")
    public static OAuthAttributes of(OAuthProvider provider, String userNameAttributeName, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> ofGoogle(attributes);
            case KAKAO -> ofKakao(attributes);
            case NAVER -> ofNaver(attributes);
        };
    }

    private static OAuthAttributes ofGoogle(Map<String, Object> attributes) {
        return new OAuthAttributes(
                String.valueOf(attributes.get("sub")),
                (String) attributes.get("email"),
                (String) attributes.getOrDefault("name", "구글회원")
        );
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.getOrDefault("kakao_account", Map.of());
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.getOrDefault("profile", Map.of());

        return new OAuthAttributes(
                String.valueOf(attributes.get("id")),
                (String) kakaoAccount.get("email"),
                (String) profile.getOrDefault("nickname", "카카오회원")
        );
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.getOrDefault("response", Map.of());

        // 네이버는 "이름(실명, name)"과 "닉네임(별명, nickname)"이 서로 다른 필드/동의항목이라
        // 실명이 아니라 닉네임 쪽을 읽어야 한다.
        return new OAuthAttributes(
                (String) response.get("id"),
                (String) response.get("email"),
                (String) response.getOrDefault("nickname", "네이버회원")
        );
    }
}
