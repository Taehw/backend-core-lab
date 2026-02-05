package com.seongho.backend_core_lab.global.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component // 스프링 빈으로 등록
public class PasswordEncoder {
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // BCrypt 암호화 객체
    
    public PasswordEncoder() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }
    
    /**
     * 평문 비밀번호를 BCrypt로 암호화
     * 
     * BCrypt 특징:
     * - 단방향 해시 함수 (암호화만 가능, 복호화 불가능)
     * - Salt 자동 생성 (같은 비밀번호도 매번 다른 해시값 생성)
     * - 보안성이 높아 무차별 대입 공격에 강함
     * 
     * @param rawPassword 평문 비밀번호 / 사용자가 입력한 비밀번호
     * @return BCrypt로 암호화된 비밀번호 / 암호화된 비밀번호
     */
    public String encode(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }
    
    /**
     * 평문 비밀번호와 암호화된 비밀번호가 일치하는지 검증
     * 
     * BCrypt는 Salt가 자동으로 포함되어 있어서
     * 저장된 해시값과 비교할 수 있습니다.
     * 
     * @param rawPassword 평문 비밀번호
     * @param encodedPassword 암호화된 비밀번호
     * @return 일치하면 true, 불일치하면 false
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
