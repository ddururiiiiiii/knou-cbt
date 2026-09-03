package knou.cbt.domain.member.model;

import lombok.Getter;

@Getter
public enum OAuthProvider {
    GOOGLE("구글"), KAKAO("카카오"), NAVER("네이버");

    private final String description;

    OAuthProvider(String description) {
        this.description = description;
    }

    public static OAuthProvider fromRegistrationId(String registrationId) {
        return OAuthProvider.valueOf(registrationId.toUpperCase());
    }
}
