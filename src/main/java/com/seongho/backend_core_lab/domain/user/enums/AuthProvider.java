package com.seongho.backend_core_lab.domain.user.enums;

/**
 * 인증 제공자(Authentication Provider)를 정의하는 ENUM
 * 
 * 사용자가 어떤 방식으로 가입/로그인했는지 구분합니다.
 * 이를 통해 일반 로그인과 소셜 로그인 사용자를 구분할 수 있습니다.
 */
public enum AuthProvider {
    /**
     * 자체 로그인 시스템
     * - 아이디/비밀번호로 직접 가입한 사용자
     * - username, password 필드를 사용
     */
    LOCAL,
    
    /**
     * Google OAuth 로그인
     * - Google 계정으로 로그인한 사용자
     * - providerId에 Google의 고유 ID 저장
     */
    GOOGLE,
    
    /**
     * GitHub OAuth 로그인
     * - GitHub 계정으로 로그인한 사용자
     * - providerId에 GitHub의 고유 ID 저장
     */
    GITHUB,
    
    /**
     * Kakao OAuth 로그인
     * - Kakao 계정으로 로그인한 사용자
     * - providerId에 Kakao의 고유 ID 저장
     */
    KAKAO
}
