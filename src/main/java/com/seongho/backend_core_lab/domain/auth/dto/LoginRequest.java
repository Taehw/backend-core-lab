package com.seongho.backend_core_lab.domain.auth.dto;

import jakarta.validation.constraints.NotBlank; // 비어있지 않은 문자열 검증
import lombok.Getter;
import lombok.NoArgsConstructor; // 기본 생성자 자동 생성

@Getter // 모든 필드에 대한 Getter 메서드 자동 생성 -> 추후에 컴파일이후 class 파일에서 자동으로 생성됨
@NoArgsConstructor // 기본 생성자 자동 생성
public class LoginRequest {
    
    @NotBlank(message = "아이디를 입력해주세요") // 비어있지 않은 문자열 검증, spring validation 라이브러리 사용
    private String username;
    
    @NotBlank(message = "비밀번호를 입력해주세요") // 비어있지 않은 문자열 검증
    private String password;
    
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
