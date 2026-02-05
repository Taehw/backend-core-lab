package com.seongho.backend_core_lab.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    
    //각 필드마다 유효성 검증 어노테이션 적용
    
    @NotBlank(message = "아이디를 입력해주세요")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다") // spring validation 라이브러리 사용 
    private String username;
    
    @NotBlank(message = "비밀번호를 입력해주세요") // spring validation 라이브러리 사용 
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다")
    private String password;
    
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    public SignupRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
