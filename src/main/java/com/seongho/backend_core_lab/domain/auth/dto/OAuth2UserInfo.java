package com.seongho.backend_core_lab.domain.auth.dto;

import java.util.Map;

/**
 * OAuth2 Provider로부터 받은 사용자 정보를 추상화하는 인터페이스
 * 
 * 각 Provider(Google, Kakao 등)마다 응답 JSON 구조가 다르지만,
 * 이 인터페이스를 통해 동일한 방식으로 사용자 정보를 추출할 수 있습니다.
 * 
 * 예시:
 * - Google: { "sub": "123", "email": "user@gmail.com", "name": "홍길동" }
 * - Kakao: { "id": 456, "kakao_account": { "email": "...", "profile": { "nickname": "..." } } }
 * 
 * 위 두 응답을 각각 GoogleOAuth2UserInfo, KakaoOAuth2UserInfo로 파싱하면
 * userInfo.getEmail()로 동일하게 이메일을 추출할 수 있습니다.
 */
public interface OAuth2UserInfo {
    
    /**
     * Provider가 제공하는 사용자 고유 ID 반환
     * 
     * - Google: "sub" 필드 (Subject의 약자)
     * - Kakao: "id" 필드
     * 
     * 이 값은 providerId로 저장되어 소셜 로그인 사용자를 식별하는 데 사용됩니다.
     */
    String getProviderId();
    
    /**
     * 사용자 이메일 반환
     * 
     * OAuth Provider에 따라 이메일 제공 여부가 다를 수 있으므로 null 가능성 고려 필요
     */
    String getEmail();
    
    /**
     * 사용자 이름(또는 닉네임) 반환
     * 
     * - Google: "name" 필드
     * - Kakao: "kakao_account.profile.nickname" 필드
     */
    String getName();
    
    /**
     * 원본 attributes 반환 (필요 시 추가 정보 접근용)
     * 
     * OAuth Provider의 전체 응답 데이터를 Map 형태로 보관
     * 나중에 프로필 사진, 생일 등 추가 정보가 필요할 때 사용 가능
     */
    Map<String, Object> getAttributes();
}
