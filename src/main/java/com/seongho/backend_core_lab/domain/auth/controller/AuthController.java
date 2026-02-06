package com.seongho.backend_core_lab.domain.auth.controller;

import com.seongho.backend_core_lab.domain.auth.dto.LoginRequest;
import com.seongho.backend_core_lab.domain.auth.dto.LoginResponse;
import com.seongho.backend_core_lab.domain.auth.dto.SignupRequest;
import com.seongho.backend_core_lab.domain.auth.dto.SignupResponse;
import com.seongho.backend_core_lab.domain.auth.service.AuthService;
import com.seongho.backend_core_lab.global.jwt.JwtTokenProvider;

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
    private final JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserId(token);
        authService.logout(userId);
        return ResponseEntity.ok("로그아웃되었습니다");
    }
}
