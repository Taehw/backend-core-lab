package com.seongho.backend_core_lab.global.filter;

import com.seongho.backend_core_lab.global.auth.SessionInfo;
import com.seongho.backend_core_lab.global.auth.SessionStore;
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
    
    private final SessionStore sessionStore;
    
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/auth/signup",
            "/auth/login"
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
        
        String sessionId = httpRequest.getHeader("X-Session-Id");
        //HTTP 요청 헤더에서 세션 ID 추출
        //세션 ID가 없으면 401 응답
        if (sessionId == null || sessionId.isEmpty()) {
            log.warn("[Filter] 세션 ID 없음, 401 반환");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"error\": \"인증이 필요합니다\"}");
            return;
        }
        
        SessionInfo sessionInfo = sessionStore.getSession(sessionId).orElse(null);
        //세션 ID로 세션 저장소에서 세션 정보 조회

        if (sessionInfo == null) {
            log.warn("[Filter] 유효하지 않은 세션 ID, 401 반환"); // 로그 출력 - Lombok의 @Slf4j 어노테이션 사용
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"error\": \"유효하지 않은 세션입니다\"}");
            return;
        } //세션 ID가 유효하지 않으면 401 응답
        
        log.info("[Filter] 인증 성공 - 사용자: {}, 권한: {}", sessionInfo.getUsername(), sessionInfo.getRole());
        
        httpRequest.setAttribute("sessionInfo", sessionInfo); // 세션 정보를 요청 속성에 저장
        
        chain.doFilter(request, response); //다음 필터로 이동
    }
    
    private boolean isPublicPath(String requestURI) {
        return PUBLIC_PATHS.stream()
                .anyMatch(requestURI::startsWith);
    }
}
