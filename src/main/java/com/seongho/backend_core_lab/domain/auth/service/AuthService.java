package com.seongho.backend_core_lab.domain.auth.service;

import com.seongho.backend_core_lab.domain.auth.dto.LoginRequest;
import com.seongho.backend_core_lab.domain.auth.dto.LoginResponse;
import com.seongho.backend_core_lab.domain.auth.dto.SignupRequest;
import com.seongho.backend_core_lab.domain.auth.dto.SignupResponse;
import com.seongho.backend_core_lab.domain.user.entity.User;
import com.seongho.backend_core_lab.domain.user.enums.AuthProvider;
import com.seongho.backend_core_lab.domain.user.enums.Role;
import com.seongho.backend_core_lab.domain.user.repository.UserRepository;
import com.seongho.backend_core_lab.global.auth.SessionInfo;
import com.seongho.backend_core_lab.global.auth.SessionStore;
import com.seongho.backend_core_lab.global.util.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
//readOnly = true: 읽기 전용 트랜잭션, 쓰기 작업 시 예외 발생
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionStore sessionStore;
    
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
    
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다");
        }
        
        SessionInfo sessionInfo = new SessionInfo(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
        
        String sessionId = sessionStore.createSession(sessionInfo); // 세션 생성
        
        return new LoginResponse(user, sessionId); // 로그인 응답 반환
    }
    
    public void logout(String sessionId) {
        sessionStore.removeSession(sessionId);
    }
}
