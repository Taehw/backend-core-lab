package com.seongho.backend_core_lab.domain.user.enums;

/**
 * 사용자 권한을 정의하는 ENUM
 * 
 * RBAC (Role-Based Access Control) 시스템에서 사용됩니다.
 * - ADMIN: 시스템 관리자 권한 (모든 기능 접근 가능)
 * - USER: 일반 사용자 권한 (제한된 기능만 접근 가능)
 */
public enum Role {
    /**
     * 시스템 관리자
     * - Admin 전용 페이지 접근 가능
     * - 시스템 설정 및 관리 기능 사용 가능
     */
    ADMIN,
    
    /**
     * 일반 사용자
     * - 자체 로그인 사용자
     * - 소셜 로그인 사용자
     * - 기본 기능만 사용 가능
     */
    USER
}
