package com.seongho.backend_core_lab.domain.user.repository;

import com.seongho.backend_core_lab.domain.user.entity.User;
import com.seongho.backend_core_lab.domain.user.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User 엔티티를 위한 Repository 인터페이스
 * 
 * JpaRepository를 상속받아 기본 CRUD 기능을 자동으로 제공받습니다.
 * - save() : 저장/수정
 * - findById() : ID로 조회
 * - findAll() : 전체 조회
 * - delete() : 삭제
 * - count() : 개수 조회
 * 등등...
 * 
 * 추가로 필요한 커스텀 메서드를 정의합니다.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * username으로 사용자 조회
     * 
     * 사용 시나리오: 일반 로그인 시 아이디로 사용자 찾기
     * 
     * @param username 사용자 아이디
     * @return 사용자 Optional (없으면 Optional.empty())
     * 
     * 메서드 네이밍 규칙:
     * - findBy + 필드명 : JPA가 자동으로 쿼리 생성
     * - 실제 실행 쿼리: SELECT * FROM users WHERE username = ?
     */
    Optional<User> findByUsername(String username);
    
    /**
     * email로 사용자 조회
     * 
     * 사용 시나리오:
     * 1. 회원가입 시 이메일 중복 체크
     * 2. 소셜 로그인 시 이메일로 기존 사용자 확인
     * 
     * @param email 이메일
     * @return 사용자 Optional
     */
    Optional<User> findByEmail(String email);
    
    /**
     * provider와 providerId로 사용자 조회
     * 
     * 사용 시나리오: 소셜 로그인 시 해당 소셜 계정으로 가입한 사용자 찾기
     * 
     * @param provider 인증 제공자 (GOOGLE, GITHUB 등)
     * @param providerId 소셜 로그인 제공자의 고유 ID
     * @return 사용자 Optional
     * 
     * 메서드 네이밍 규칙:
     * - And로 여러 조건 연결
     * - 실제 실행 쿼리: SELECT * FROM users WHERE provider = ? AND provider_id = ?
     */
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
    
    /**
     * username 존재 여부 확인
     * 
     * 사용 시나리오: 회원가입 시 아이디 중복 체크
     * 
     * @param username 사용자 아이디
     * @return 존재하면 true, 없으면 false
     * 
     * 메서드 네이밍 규칙:
     * - existsBy + 필드명 : boolean 반환
     * - 실제 실행 쿼리: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     */
    boolean existsByUsername(String username);
    
    /**
     * email 존재 여부 확인
     * 
     * 사용 시나리오: 회원가입 시 이메일 중복 체크
     * 
     * @param email 이메일
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByEmail(String email);
}
