package com.seongho.backend_core_lab.domain.auth.dto;

import com.seongho.backend_core_lab.domain.user.entity.User;
import com.seongho.backend_core_lab.domain.user.enums.Role;
import lombok.Getter;

@Getter
public class SignupResponse {
    
    private final Long userId;
    private final String username;
    private final String email;
    private final Role role;
    private final String message;
    
    //로그인 응답과 동일하게 password 필드는 제외하고 생성자 생성 -> 보안 유지
    public SignupResponse(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole(); //기본 Role: USER
        this.message = "회원가입이 완료되었습니다";
    }
}
