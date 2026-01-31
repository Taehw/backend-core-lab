package com.seongho.backend_core_lab.global.auth;

import com.seongho.backend_core_lab.domain.user.enums.Role;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SessionInfo {
    
    private final Long userId;
    private final String username;
    private final Role role;
    private final LocalDateTime loginTime;
    
    public SessionInfo(Long userId, String username, Role role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.loginTime = LocalDateTime.now(); // 로그인 시간 저장
    }
    
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
}
