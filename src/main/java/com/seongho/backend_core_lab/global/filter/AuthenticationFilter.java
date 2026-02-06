package com.seongho.backend_core_lab.global.filter;

import com.seongho.backend_core_lab.domain.user.enums.Role;
import com.seongho.backend_core_lab.global.auth.SessionInfo;
import com.seongho.backend_core_lab.global.jwt.JwtTokenProvider;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest; // HTTP 요청을 처리하는 클래스
import jakarta.servlet.http.HttpServletResponse; // HTTP 요청과 응답을 처리하는 클래스
import lombok.RequiredArgsConstructor; // Lombok의 @RequiredArgsConstructor 어노테이션 사용 -> 생성자 자동 생성
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component; // Spring의 @Component 어노테이션 사용 -> 스프링 컨테이너에 빈으로 등록

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j // Lombok의 @Slf4j 어노테이션 사용 -> 로그 출력 용이
/*
자동으로 logger 객체 생성
콘솔에 로그 출력
디버깅 및 모니터링에 유용용
*/

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {
    
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성/파싱을 위한 프로바이더
    
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/auth/signup",
            "/auth/login",
            "/auth/refresh" //Refresh Token 갱신 경로 - 인증없이 접근 가능능
    );
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        
        log.info("[Filter] 요청 URI: {}", requestURI);
        
        if (isPublicPath(requestURI)) { //회원가입/로그인 경로는 인증 불필요
            log.info("[Filter] 인증 불필요 경로, 통과"); // 로그 출력
            chain.doFilter(request, response); //다음 필터로 이동
            return;
        }
        
        String authorizationHeader = httpRequest.getHeader("Authorization");
        
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) { //Authorization 헤더 없거나 Bearer 접두사 없으면 401 반환
            log.warn("[Filter] Authorization 헤더 없음, 401 반환");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"error\": \"인증이 필요합니다\"}");
            return;
        }
        
        String token = authorizationHeader.substring(7); // "Bearer " 제거
        
        //JWT 토큰 검증
        if (!jwtTokenProvider.validateToken(token)) { //JWT 토큰 검증 실패 시 401 반환 -> 1. 서명검증, 2. 만료확인, 3. JWT 구조 확인인
            log.warn("[Filter] 유효하지 않은 JWT 토큰, 401 반환");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401 반환
            httpResponse.setContentType("application/json;charset=UTF-8"); //JSON 형식으로 응답
            httpResponse.getWriter().write("{\"error\": \"유효하지 않은 토큰입니다\"}"); //에러 메시지 반환
            return;
        }
        
        //JWT 토큰에서 사용자 정보(Claims) 추출
        Long userId = jwtTokenProvider.getUserId(token);
        String username = jwtTokenProvider.getUsername(token);
        Role role = jwtTokenProvider.getRole(token);
        
        SessionInfo sessionInfo = new SessionInfo(userId, username, role); // 세션정보는 그대로 사용 -> 로그인 시 생성된 세션 정보 재사용
        
        log.info("[Filter] JWT 인증 성공 - 사용자: {}, 권한: {}", username, role);
        
        httpRequest.setAttribute("sessionInfo", sessionInfo); // 세션정보를 요청 속성에 저장
        
        chain.doFilter(request, response); //다음 필터로 이동
    }
    
    private boolean isPublicPath(String requestURI) {
        return PUBLIC_PATHS.stream()
                .anyMatch(requestURI::startsWith);
    }
}
