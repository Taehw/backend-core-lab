package com.seongho.backend_core_lab.domain.user.entity;

import com.seongho.backend_core_lab.domain.user.enums.AuthProvider;
import com.seongho.backend_core_lab.domain.user.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자(User) 엔티티
 * 
 * 일반 로그인 사용자와 소셜 로그인 사용자를 모두 관리하는 테이블입니다.
 * 
 * <주요 필드 설명>
 * - id: 기본키, 자동 증가
 * - username: 일반 로그인 시 사용하는 아이디 (소셜 로그인은 null 가능)
 * - password: 비밀번호 해시값 (소셜 로그인은 null 가능)
 * - email: 이메일 (모든 사용자 필수, 고유값)
 * - role: 권한 (ADMIN 또는 USER)
 * - provider: 인증 제공자 (LOCAL, GOOGLE, GITHUB 등)
 * - providerId: 소셜 로그인 시 제공자에서 받은 고유 ID
 * - createdAt: 계정 생성 시간
 */
@Entity
@Table(name = "users")  // 'user'는 예약어일 수 있으므로 'users' 사용
@Getter // 모든 필드에 대한 Getter 메서드 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA를 위한 기본 생성자, 외부 생성 방지
public class User {
    
    /**
     * 기본키 (Primary Key)
     * @GeneratedValue: 자동 증가 전략 사용
     * IDENTITY: 데이터베이스의 AUTO_INCREMENT 기능 활용 (H2, MySQL 등)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 로그인 아이디
     * - 일반 로그인(LOCAL) 사용자만 사용
     * - 소셜 로그인 사용자는 null
     * - 고유값이므로 unique = true 설정
     */
    @Column(unique = true, length = 50) // unique = true: 중복 불가, length = 50: 최대 50자 저장
    private String username;
    
    /**
     * 비밀번호 (해시값)
     * - 일반 로그인(LOCAL) 사용자만 사용
     * - 소셜 로그인 사용자는 null
     * - 평문이 아닌 해시된 값을 저장 (나중에 BCrypt 적용 예정) // 평문으로 저장하면 안되는 이유: 보안 유지
     */
    @Column(length = 255)
    private String password;
    
    /**
     * 이메일
     * - 모든 사용자 필수
     * - 고유값 (중복 가입 방지)
     * - 소셜 로그인 시 주요 식별자로 사용
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    /**
     * 사용자 권한
     * @Enumerated(EnumType.STRING): ENUM을 문자열로 저장
     * - ORDINAL(숫자)이 아닌 STRING 사용 이유:
     *   ENUM 순서가 바뀌어도 데이터 일관성 유지
     * - 기본값: USER -> 아래 생성자에서 기본값 설정
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    
    /**
     * 인증 제공자
     * - LOCAL: 자체 로그인 시스템
     * - GOOGLE, GITHUB, KAKAO: 소셜 로그인
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;
    
    /**
     * 소셜 로그인 제공자의 고유 ID
     * - 일반 로그인(LOCAL)은 null
     * - 소셜 로그인 시 해당 플랫폼에서 제공하는 사용자 ID 저장
     * 예: Google의 경우 sub 클레임 값
     */
    @Column(length = 255)
    private String providerId;
    
    /**
     * 계정 생성 시간
     * @CreationTimestamp: 엔티티 생성 시 자동으로 현재 시간 저장
     * updatable = false: 생성 후 변경 불가
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Builder 패턴을 사용한 생성자
     * 
     * Lombok의 @Builder 대신 직접 구현하여 기본값 설정
     * - role의 기본값: USER
     * - provider의 기본값: LOCAL
     */
    @Builder
    public User(String username, String password, String email, 
               Role role, AuthProvider provider, String providerId) {
        this.username = username; // 일반 로그인 시 사용하는 아이디
        this.password = password; // 비밀번호 해시값
        this.email = email; // 이메일
        this.role = (role != null) ? role : Role.USER;  // 기본값: USER
        this.provider = (provider != null) ? provider : AuthProvider.LOCAL;  // 기본값: LOCAL
        this.providerId = providerId; // 소셜 로그인 시 제공자에서 받은 고유 ID
    }
    
    /**
     * 비밀번호 업데이트 메서드
     * - 비밀번호 변경 기능에서 사용
     * - 해시된 비밀번호를 전달받아야 함
     */
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
    
    /**
     * 권한 변경 메서드
     * - 관리자가 사용자 권한을 변경할 때 사용
     */
    public void updateRole(Role role) {
        this.role = role;
    }
}
