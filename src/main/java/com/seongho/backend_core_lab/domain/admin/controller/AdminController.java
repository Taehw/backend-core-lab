package com.seongho.backend_core_lab.domain.admin.controller;

import com.seongho.backend_core_lab.global.auth.SessionInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin 전용 컨트롤러
 * 
 * AdminAuthorizationInterceptor에 의해 ADMIN 권한 체크가 자동으로 수행됩니다.
 * WebConfig에서 /admin/** 경로에 Interceptor가 등록되어 있습니다.
 * 
 * <접근 제어>
 * - Filter: 세션 인증 체크 (모든 요청)
 * - Interceptor: ADMIN 권한 체크 (/admin/** 경로만)
 * 
 * <엔드포인트>
 * - GET /admin: Admin 페이지 메인
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    /**
     * Admin 페이지 메인
     * 
     * ADMIN 권한을 가진 사용자만 접근 가능합니다.
     * Filter와 Interceptor를 통과한 경우에만 이 메서드가 실행됩니다.
     * 
     * @param request HttpServletRequest (Filter에서 설정한 sessionInfo 포함)
     * @return 환영 메시지
     */
    @GetMapping
    public String adminPage(HttpServletRequest request) {
        SessionInfo sessionInfo = (SessionInfo) request.getAttribute("sessionInfo");
        
        return String.format("Hello World, %s님 (관리자)", sessionInfo.getUsername());
    }
}
