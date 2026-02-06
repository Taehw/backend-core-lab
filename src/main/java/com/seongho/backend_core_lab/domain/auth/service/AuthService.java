package com.seongho.backend_core_lab.domain.auth.service;

import com.seongho.backend_core_lab.domain.auth.dto.LoginRequest;
import com.seongho.backend_core_lab.domain.auth.dto.LoginResponse;
import com.seongho.backend_core_lab.domain.auth.dto.SignupRequest;
import com.seongho.backend_core_lab.domain.auth.dto.SignupResponse;
import com.seongho.backend_core_lab.domain.auth.entity.RefreshToken;
import com.seongho.backend_core_lab.domain.auth.repository.RefreshTokenRepository;
import com.seongho.backend_core_lab.domain.user.entity.User;
import com.seongho.backend_core_lab.domain.user.enums.AuthProvider;
import com.seongho.backend_core_lab.domain.user.enums.Role;
import com.seongho.backend_core_lab.domain.user.repository.UserRepository;
import com.seongho.backend_core_lab.global.config.JwtProperties;
import com.seongho.backend_core_lab.global.jwt.JwtTokenProvider;
import com.seongho.backend_core_lab.global.util.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션
public class AuthService {
    
    private final UserRepository userRepository; // 사용자 저장/조회를 위한 리포지토리
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성/파싱을 위한 프로바이더
    private final RefreshTokenRepository refreshTokenRepository; // Refresh Token 저장/조회를 위한 리포지토리
    private final JwtProperties jwtProperties; // JWT 토큰 생성/파싱을 위한 프로퍼티
    
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }
        
        String encodedPassword = passwordEncoder.encode(request.getPassword()); // 비밀번호 암호화
        
        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword) // 비밀번호 암호화 저장
                .email(request.getEmail())
                .role(Role.USER) // 기본 Role: USER
                .provider(AuthProvider.LOCAL)
                .build();
        
        User savedUser = userRepository.save(user); // 사용자 저장
        
        return new SignupResponse(savedUser); // 회원가입 응답 반환
    }
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다");
        }
        
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
        
        String refreshTokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000);
        
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .map(existing -> {
                    existing.updateToken(refreshTokenValue, expiresAt);
                    return existing;
                })
                .orElseGet(() -> {
                    RefreshToken newToken = RefreshToken.builder()
                            .token(refreshTokenValue)
                            .user(user)
                            .expiresAt(expiresAt)
                            .build();
                    return refreshTokenRepository.save(newToken);
                });
        
        return new LoginResponse(user, accessToken, refreshToken.getToken());
    }
    
    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        refreshTokenRepository.deleteByUser(user);
    }
}
