package com.seongho.backend_core_lab.domain.auth.dto;

import com.seongho.backend_core_lab.domain.user.entity.User;
import com.seongho.backend_core_lab.domain.user.enums.Role;
import lombok.Getter;

@Getter
public class LoginResponse {
    
    //final 키워드를 사용하여 불변성 보장
    private final Long userId;
    private final String username;
    private final String email;
    private final Role role;
    private final String sessionId;
    
    public LoginResponse(User user, String sessionId) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.sessionId = sessionId;
    } // password 필드는 제외하고 생성자 생성 -> 보안 유지
}
