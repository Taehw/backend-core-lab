package com.seongho.backend_core_lab.global.config;

import com.seongho.backend_core_lab.domain.auth.handler.OAuth2SuccessHandler;
import com.seongho.backend_core_lab.domain.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스
 * 
 * 이 클래스의 역할:
 * 1. OAuth2 로그인 활성화 및 설정
 * 2. CustomOAuth2UserService와 OAuth2SuccessHandler 연결
 * 3. 기존 AuthenticationFilter와 공존 설정
 * 4. Public 엔드포인트 설정
 * 
 * 기존 프로젝트와의 통합:
 * - 기존: AuthenticationFilter (JWT 검증 필터)
 * - 추가: Spring Security OAuth2 (소셜 로그인)
 * - 두 방식이 충돌하지 않도록 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    
    /**
     * Spring Security의 필터 체인 설정
     * 
     * SecurityFilterChain:
     * - Spring Security의 핵심 필터 체인
     * - HTTP 요청이 들어오면 여러 필터를 거쳐서 처리됨
     * - 우리는 OAuth2 관련 필터만 활성화하고 나머지는 비활성화
     * 
     * @param http HttpSecurity 설정 객체
     * @return SecurityFilterChain 설정된 필터 체인
     * @throws Exception 설정 오류 시
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                // REST API는 stateless하므로 CSRF 보호 불필요
                // (우리는 JWT 사용, 세션 기반 인증 아님)
                .csrf(AbstractHttpConfigurer::disable)
                
                // HTTP Basic 인증 비활성화
                // (우리는 JWT와 OAuth2 사용)
                .httpBasic(AbstractHttpConfigurer::disable)
                
                // Form 로그인 비활성화
                // (프론트엔드가 따로 있고, REST API만 제공)
                .formLogin(AbstractHttpConfigurer::disable)
                
                // 로그아웃 비활성화
                // (우리는 /auth/logout API로 직접 처리)
                .logout(AbstractHttpConfigurer::disable)
                
                // 인가(Authorization) 설정
                .authorizeHttpRequests(authorize -> authorize
                        // OAuth2 관련 엔드포인트는 모두 허용
                        .requestMatchers(
                                "/oauth2/**",              // OAuth2 로그인 시작 (/oauth2/authorization/google)
                                "/login/oauth2/code/**"    // OAuth2 콜백 (/login/oauth2/code/google)
                        ).permitAll()
                        
                        // 기존 AuthenticationFilter의 PUBLIC_PATHS도 허용
                        .requestMatchers(
                                "/auth/signup",
                                "/auth/login",
                                "/auth/refresh"
                        ).permitAll()
                        
                        // H2 콘솔 허용 (개발 환경)
                        .requestMatchers("/h2-console/**").permitAll()
                        
                        // 나머지 요청은 AuthenticationFilter에서 처리
                        // Spring Security는 이 요청들을 통과시키고,
                        // AuthenticationFilter가 JWT 검증함
                        .anyRequest().permitAll()
                )
                
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        // OAuth2 로그인 엔드포인트 설정
                        // /oauth2/authorization/{provider} 형식으로 접근
                        // 예: /oauth2/authorization/google
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization")
                        )
                        
                        // OAuth2 콜백(리디렉션) 엔드포인트 설정
                        // OAuth Provider가 인증 후 여기로 리디렉션함
                        // 예: /login/oauth2/code/google?code=...
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*")
                        )
                        
                        // 사용자 정보 엔드포인트 설정
                        // OAuth Provider에서 사용자 정보를 가져올 때 사용할 서비스 지정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)  // 우리가 만든 서비스!
                        )
                        
                        // 로그인 성공 핸들러 설정
                        // OAuth2 로그인 성공 시 이 핸들러가 호출됨
                        .successHandler(oAuth2SuccessHandler)  // 우리가 만든 핸들러!
                )
                
                // H2 콘솔을 위한 설정 (개발 환경)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                );
        
        return http.build();
    }
}
