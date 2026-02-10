package com.seongho.backend_core_lab.domain.auth.handler;

import com.seongho.backend_core_lab.domain.auth.entity.RefreshToken;
import com.seongho.backend_core_lab.domain.auth.repository.RefreshTokenRepository;
import com.seongho.backend_core_lab.domain.user.entity.User;
import com.seongho.backend_core_lab.domain.user.repository.UserRepository;
import com.seongho.backend_core_lab.global.config.JwtProperties;
import com.seongho.backend_core_lab.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OAuth2 로그인 성공 시 처리하는 핸들러
 * 
 * Spring Security OAuth2 흐름:
 * 1. OAuth Provider에서 로그인 완료
 * 2. CustomOAuth2UserService에서 사용자 정보 DB 저장/조회
 * 3. ⭐ 이 핸들러의 onAuthenticationSuccess() 호출 ⭐
 * 4. JWT 발급 및 프론트엔드로 리디렉션
 * 
 * SimpleUrlAuthenticationSuccessHandler 상속 이유:
 * - Spring Security의 기본 성공 핸들러
 * - 리디렉션 처리 로직이 이미 구현되어 있음
 * - onAuthenticationSuccess()만 오버라이드하면 됨
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    
    /**
     * application.properties에서 주입받는 프론트엔드 리디렉션 URL
     * 예: http://localhost:3000/oauth2/redirect
     */
    @Value("${oauth2.redirect-url}")
    private String redirectUrl;
    
    /**
     * OAuth2 로그인 성공 시 자동으로 호출되는 메서드
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authentication Spring Security의 인증 객체 (OAuth2User 포함)
     * @throws IOException 리디렉션 실패 시
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        // 1. OAuth2User 추출 (CustomOAuth2UserService에서 반환한 객체)
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        // 2. OAuth2User에서 사용자 식별 정보 추출
        // Google: "sub" 필드 값
        // Kakao: "id" 필드 값
        String userNameAttribute = determineUserNameAttribute(oAuth2User);
        String providerId = oAuth2User.getAttribute(userNameAttribute);
        
        // 3. Provider 식별
        // OAuth2User의 attributes에는 어떤 Provider인지 정보가 없음
        // 그래서 request에서 추출해야 함
        String provider = extractProviderFromRequest(request);
        
        // 4. DB에서 사용자 조회 (CustomOAuth2UserService에서 이미 저장함)
        User user = userRepository.findByProviderAndProviderId(
                com.seongho.backend_core_lab.domain.user.enums.AuthProvider.valueOf(provider.toUpperCase()),
                providerId
        ).orElseThrow(() -> new IllegalStateException("OAuth2 로그인 사용자를 찾을 수 없습니다"));
        
        // 5. Access Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getEmail(),  // 소셜 로그인은 username이 null이므로 email 사용
                user.getRole()
        );
        
        // 6. Refresh Token 생성 및 DB 저장
        String refreshTokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000);
        
        // 기존 Refresh Token이 있으면 업데이트, 없으면 새로 생성
        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        existingToken -> existingToken.updateToken(refreshTokenValue, expiresAt),
                        () -> {
                            RefreshToken newToken = RefreshToken.builder()
                                    .token(refreshTokenValue)
                                    .user(user)
                                    .expiresAt(expiresAt)
                                    .build();
                            refreshTokenRepository.save(newToken);
                        }
                );
        
        // 7. 프론트엔드로 리디렉션 (쿼리 파라미터에 토큰 포함)
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshTokenValue)
                .build()
                .toUriString();
        
        log.info("OAuth2 로그인 성공 - 사용자: {}, Provider: {}", user.getEmail(), provider);
        log.info("리디렉션 URL: {}", targetUrl);
        
        // 8. 실제 리디렉션 수행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
    
    /**
     * OAuth2User에서 사용자 식별 필드명 결정
     * 
     * OAuth2User.getAttributes()에서 어떤 키를 사용자 식별자로 쓸지 결정
     * - Google: "sub" (attributes에 "sub" 키가 있음)
     * - Kakao: "id" (attributes에 "id" 키가 있음)
     * 
     * @param oAuth2User OAuth2 사용자 객체
     * @return String 사용자 식별 필드명
     */
    private String determineUserNameAttribute(OAuth2User oAuth2User) {
        // Google은 "sub" 필드 존재
        if (oAuth2User.getAttribute("sub") != null) {
            return "sub";
        }
        // Kakao는 "id" 필드 존재
        if (oAuth2User.getAttribute("id") != null) {
            return "id";
        }
        throw new IllegalStateException("지원하지 않는 OAuth Provider입니다");
    }
    
    /**
     * HTTP 요청에서 OAuth Provider 식별
     * 
     * OAuth2 로그인 후 콜백 URL: /login/oauth2/code/{provider}
     * 예: /login/oauth2/code/google
     *     /login/oauth2/code/kakao
     * 
     * 이 URL에서 provider 부분을 추출
     * 
     * @param request HTTP 요청
     * @return String Provider 이름 ("google", "kakao")
     */
    private String extractProviderFromRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        
        // /login/oauth2/code/google → "google"
        // /login/oauth2/code/kakao → "kakao"
        if (requestUri.contains("/login/oauth2/code/")) {
            String[] parts = requestUri.split("/");
            return parts[parts.length - 1];
        }
        
        throw new IllegalStateException("OAuth Provider를 식별할 수 없습니다");
    }
}
