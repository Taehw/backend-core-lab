package com.seongho.backend_core_lab.domain.auth.service;

import com.seongho.backend_core_lab.domain.auth.dto.GoogleOAuth2UserInfo;
import com.seongho.backend_core_lab.domain.auth.dto.KakaoOAuth2UserInfo;
import com.seongho.backend_core_lab.domain.auth.dto.OAuth2UserInfo;
import com.seongho.backend_core_lab.domain.user.entity.User;
import com.seongho.backend_core_lab.domain.user.enums.AuthProvider;
import com.seongho.backend_core_lab.domain.user.enums.Role;
import com.seongho.backend_core_lab.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * OAuth2 로그인 시 사용자 정보를 처리하는 커스텀 서비스
 * 
 * Spring Security OAuth2 흐름:
 * 1. 사용자가 소셜 로그인 버튼 클릭
 * 2. OAuth Provider(Google, Kakao)에서 인증
 * 3. Authorization Code 받음
 * 4. Spring Security가 자동으로 Access Token 교환
 * 5. Spring Security가 자동으로 사용자 정보 조회
 * 6. ⭐ 이 서비스의 loadUser() 호출 ⭐
 * 7. 우리가 처리: Provider 식별 → DTO 변환 → DB 저장/조회
 * 8. OAuth2User 반환 → SuccessHandler로 이동
 * 
 * DefaultOAuth2UserService 상속 이유:
 * - Spring Security의 기본 OAuth2 사용자 로딩 로직 활용
 * - loadUser()만 오버라이드하여 우리 DB와 연동
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserRepository userRepository;
    
    /**
     * OAuth2 Provider로부터 받은 사용자 정보를 처리하고 DB에 저장/조회
     * 
     * @param userRequest OAuth2 로그인 요청 정보 (어떤 Provider인지, Access Token 등 포함)
     * @return OAuth2User Spring Security가 인증 정보로 사용할 사용자 객체
     * @throws OAuth2AuthenticationException OAuth2 인증 실패 시
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 부모 클래스의 loadUser()로 OAuth Provider에서 사용자 정보 가져오기
        // 이 메서드가 자동으로 Access Token을 사용해 userInfo endpoint 호출
        OAuth2User oAuth2User = super.loadUser(userRequest); 
        
        // 2. 어떤 Provider인지 식별 (google, kakao 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        // 3. OAuth Provider가 응답한 사용자 정보를 Map으로 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // 4. Provider별로 적절한 DTO 생성 및 DB 처리
        OAuth2UserInfo userInfo = getOAuth2UserInfo(registrationId, attributes);
        User user = saveOrUpdate(userInfo, registrationId);
        
        // 5. Spring Security가 사용할 OAuth2User 반환
        // DefaultOAuth2User는 Spring Security가 제공하는 기본 구현체
        // - authorities: 권한 목록 (ROLE_USER)
        // - attributes: OAuth Provider의 원본 응답 데이터
        // - nameAttributeKey: 사용자를 식별하는 필드명 (Google: "sub", Kakao: "id")
        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                java.util.Collections.singleton(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                ),
                attributes, //OAuth Provider의 원본 응답 데이터
                getUserNameAttributeName(registrationId) //사용자를 식별하는 필드명 (Google: "sub", Kakao: "id")
        );
    }
    
    /**
     * Provider에 따라 적절한 OAuth2UserInfo 구현체 생성
     * 
     * @param registrationId Provider 식별자 ("google", "kakao")
     * @param attributes OAuth Provider의 사용자 정보 응답
     * @return OAuth2UserInfo 공통 인터페이스로 래핑된 사용자 정보
     */
    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equals("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equals("kakao")) {
            return new KakaoOAuth2UserInfo(attributes);
        } else {
            throw new IllegalArgumentException("지원하지 않는 OAuth Provider입니다: " + registrationId);
        }
    }
    
    /**
     * 사용자 정보를 DB에 저장하거나 업데이트
     * 
     * 로직:
     * - Provider + ProviderId로 기존 사용자 조회
     * - 있으면: 정보 업데이트 (이메일, 이름 변경 가능)
     * - 없으면: 신규 회원가입
     * 
     * @param userInfo OAuth Provider에서 받은 사용자 정보
     * @param registrationId Provider 식별자
     * @return User 저장/업데이트된 사용자 엔티티
     */
    private User saveOrUpdate(OAuth2UserInfo userInfo, String registrationId) {
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase()); //Provider 식별자 대문자로 변환
        String providerId = userInfo.getProviderId();
        
        // Provider + ProviderId로 기존 사용자 찾기
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .map(existingUser -> {
                    // 기존 사용자: 이메일만 업데이트 (소셜 계정에서 이메일 변경 가능성)
                    // username은 소셜 로그인 사용자는 null이므로 업데이트 불필요
                    return existingUser;
                })
                .orElseGet(() -> {
                    // 신규 사용자: 회원가입 처리
                    User newUser = User.builder()
                            .email(userInfo.getEmail())
                            .provider(provider)
                            .providerId(providerId)
                            .role(Role.USER)
                            .build();
                    return userRepository.save(newUser);
                });
    }
    
    /**
     * Provider별 사용자 식별 필드명 반환
     * 
     * OAuth Provider마다 사용자를 식별하는 필드명이 다름:
     * - Google: "sub" (Subject, OpenID Connect 표준)
     * - Kakao: "id"
     * 
     * Spring Security는 이 필드를 OAuth2User.getName()으로 사용
     * 
     * @param registrationId Provider 식별자
     * @return String 사용자 식별 필드명
     */
    private String getUserNameAttributeName(String registrationId) {
        if (registrationId.equals("google")) {
            return "sub";
        } else if (registrationId.equals("kakao")) {
            return "id";
        } else {
            throw new IllegalArgumentException("지원하지 않는 OAuth Provider입니다: " + registrationId);
        }
    }
}
