package com.seongho.backend_core_lab.domain.auth.dto;

import com.seongho.backend_core_lab.domain.user.entity.User;
import com.seongho.backend_core_lab.domain.user.enums.Role;
import lombok.Getter;

@Getter
public class LoginResponse {
    
    private final Long userId;
    private final String username;
    private final String email;
    private final Role role;
    private final String accessToken;
    private final String refreshToken;
    
    public LoginResponse(User user, String accessToken, String refreshToken) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
