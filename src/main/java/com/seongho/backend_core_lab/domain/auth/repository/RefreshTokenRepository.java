package com.seongho.backend_core_lab.domain.auth.repository;

import com.seongho.backend_core_lab.domain.auth.entity.RefreshToken;
import com.seongho.backend_core_lab.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> { 
    
//Optional: 값이 없을 수 있음을 나타내는 클래스

    Optional<RefreshToken> findByToken(String token); 
    
    Optional<RefreshToken> findByUser(User user);
    
    void deleteByUser(User user);
}
