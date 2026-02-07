package com.seongho.backend_core_lab.domain.auth.dto;

import java.util.Map;

/**
 * Google OAuth2로부터 받은 사용자 정보를 처리하는 구현체
 * 
 * Google UserInfo Endpoint 응답 예시:
 * {
 *   "sub": "1234567890",           // Google 고유 ID
 *   "name": "홍길동",
 *   "given_name": "길동",
 *   "family_name": "홍",
 *   "email": "hong@gmail.com",
 *   "email_verified": true,
 *   "picture": "https://lh3.googleusercontent.com/..."
 * }
 * 
 * 참고: Google의 경우 "sub" 필드가 사용자 고유 식별자입니다.
 * (Subject의 약자, OpenID Connect 표준 용어)
 */
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
    
    private final Map<String, Object> attributes;
    
    /**
     * Google에서 받은 사용자 정보 Map을 저장
     * 
     * @param attributes Spring Security OAuth2가 자동으로 파싱한 사용자 정보
     */
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }
    
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
    
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
