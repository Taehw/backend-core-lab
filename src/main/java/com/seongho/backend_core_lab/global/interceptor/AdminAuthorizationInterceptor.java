package com.seongho.backend_core_lab.global.interceptor;

import com.seongho.backend_core_lab.global.auth.SessionInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AdminAuthorizationInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        SessionInfo sessionInfo = (SessionInfo) request.getAttribute("sessionInfo"); // 세션 정보를 요청 속성에서 추출
        //Filter에서 세션 정보를 요청 속성에 저장했기 때문에 여기서 추출 가능
        
        if (sessionInfo == null) {
            log.warn("[Interceptor] 세션 정보 없음, 401 반환"); // 로그 출력 - Lombok의 @Slf4j 어노테이션 사용
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 응답
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"인증이 필요합니다\"}");
            return false;
        }
        
        if (!sessionInfo.isAdmin()) { // 세션 정보에 저장된 권한이 ADMIN이 아니면 403 응답
            log.warn("[Interceptor] ADMIN 권한 없음 - 사용자: {}, 권한: {}", 
                    sessionInfo.getUsername(), sessionInfo.getRole()); // 로그 출력 - Lombok의 @Slf4j 어노테이션 사용
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 응답
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"관리자 권한이 필요합니다\"}");
            return false;
        }
        
        log.info("[Interceptor] ADMIN 권한 확인 완료 - 사용자: {}", sessionInfo.getUsername());
        return true;
    }
}
