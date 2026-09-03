package knou.cbt.domain.member.model;

public enum OAuthProvider {
    GOOGLE, KAKAO, NAVER;

    public static OAuthProvider fromRegistrationId(String registrationId) {
        return OAuthProvider.valueOf(registrationId.toUpperCase());
    }
}
