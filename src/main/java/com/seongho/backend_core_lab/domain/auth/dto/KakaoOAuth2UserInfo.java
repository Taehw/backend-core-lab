package com.seongho.backend_core_lab.domain.auth.dto;

import java.util.Map;

/**
 * Kakao OAuth2로부터 받은 사용자 정보를 처리하는 구현체
 * 
 * Kakao UserInfo Endpoint 응답 예시:
 * {
 *   "id": 9876543210,              // Kakao 고유 ID (Long 타입)
 *   "connected_at": "2024-01-01T00:00:00Z",
 *   "kakao_account": {             // 중첩된 객체 주의!
 *     "profile_nickname_needs_agreement": false,
 *     "profile": {
 *       "nickname": "홍길동",
 *       "thumbnail_image_url": "http://...",
 *       "profile_image_url": "http://..."
 *     },
 *     "has_email": true,
 *     "email_needs_agreement": false,
 *     "is_email_valid": true,
 *     "is_email_verified": true,
 *     "email": "hong@kakao.com"
 *   }
 * }
 * 
 * 주의사항:
 * 1. id가 Long 타입이므로 String으로 변환 필요
 * 2. email과 nickname이 중첩된 객체 안에 있음
 * 3. 사용자가 정보 제공 동의를 안 하면 null일 수 있음
 */
public class KakaoOAuth2UserInfo implements OAuth2UserInfo {
    
    private final Map<String, Object> attributes;
    
    /**
     * Kakao에서 받은 사용자 정보 Map을 저장
     * 
     * @param attributes Spring Security OAuth2가 자동으로 파싱한 사용자 정보
     */
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    @Override
    public String getProviderId() {
        Object id = attributes.get("id");
        return id != null ? String.valueOf(id) : null;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account"); 
        if (kakaoAccount == null) {
            return null;
        }
        return (String) kakaoAccount.get("email");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    //캐스팅 경고 무시
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            return null;
        }
        
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) {
            return null;
        }
        
        return (String) profile.get("nickname");
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
