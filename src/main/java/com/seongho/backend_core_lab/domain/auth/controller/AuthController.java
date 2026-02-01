package com.seongho.backend_core_lab.domain.auth.controller;

import com.seongho.backend_core_lab.domain.auth.dto.LoginRequest;
import com.seongho.backend_core_lab.domain.auth.dto.LoginResponse;
import com.seongho.backend_core_lab.domain.auth.dto.SignupRequest;
import com.seongho.backend_core_lab.domain.auth.dto.SignupResponse;
import com.seongho.backend_core_lab.domain.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response); //세션 아이디를 응답으로 반환해야댐댐
    }
    
    @PostMapping("/logout")
    //HTTP 상태코드 + 응답 본문을 함게 반환
    public ResponseEntity<String> logout(@RequestHeader("X-Session-Id") String sessionId) {
        
        authService.logout(sessionId);
        return ResponseEntity.ok("로그아웃되었습니다");
        // = ResponseEntity.status(200).body(response)
    }
}
