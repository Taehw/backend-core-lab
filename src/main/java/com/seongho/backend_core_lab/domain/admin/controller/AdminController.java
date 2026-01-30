package com.seongho.backend_core_lab.domain.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin 전용 컨트롤러
 * 
 * 관리자 권한을 가진 사용자만 접근할 수 있는 API를 제공합니다.
 * (추후 Spring Security를 통해 권한 체크 추가 예정)
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
     * 관리자가 접속 시 "Hello World" 메시지를 반환합니다.
     * 
     * @return Hello World 메시지
     */
    @GetMapping
    public String adminPage() {
        return "Hello World";
    }
}
