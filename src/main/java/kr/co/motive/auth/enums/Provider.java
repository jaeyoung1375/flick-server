package kr.co.motive.auth.enums;

public enum Provider {

    KAKAO,
    GOOGLE,
    GITHUB;

    /**
     * Spring Security의 OAuth2 registrationId(소문자, 예: "kakao")를 Provider로 변환
     */
    public static Provider fromRegistrationId(String registrationId) {
        return Provider.valueOf(registrationId.toUpperCase());
    }
}
