//이코드는 JWT 토큰을 생성하고 검증하는 클래스입니다.

package com.seongho.backend_core_lab.global.jwt;

import com.seongho.backend_core_lab.domain.user.enums.Role;
import com.seongho.backend_core_lab.global.config.JwtProperties;
import io.jsonwebtoken.*; // JWT 토큰 생성/파싱을 위한 클래스
import io.jsonwebtoken.security.Keys; // JWT 서명에 사용할 비밀키 생성을 위한 클래스
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j // Lombok의 @Slf4j 어노테이션 사용 -> 로그 출력 용이
/*
자동으로 logger 객체 생성
콘솔에 로그 출력
디버깅 및 모니터링에 유용용
*/

@Component
@RequiredArgsConstructor
public class JwtTokenProvider { // JWT 토큰을 생성하고 검증하는 클래스
    
    private final JwtProperties jwtProperties; // JWT 토큰을 생성하고 검증하는 클래스
    
    public String createAccessToken(Long userId, String username, Role role) { // Access Token 생성
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());
        
        return Jwts.builder()
                .subject(userId.toString()) // 토큰 주체 (사용자 ID)
                .claim("username", username)
                .claim("role", role.name()) 
                .issuedAt(now) // 토큰 발급 시간
                .expiration(expiration) // 토큰 만료 시간
                .signWith(getSigningKey())
                .compact();
    }
    
    public boolean validateToken(String token) { // JWT 토큰 검증하는 메서드
        try { 
            Jwts.parser() // JWT 파서 생성
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 토큰: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("JWT 서명 검증 실패: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있음: {}", e.getMessage());
        }
        return false;
    }
    
    public Long getUserId(String token) { // JWT 토큰에서 사용자 ID 추출
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }
    
    public String getUsername(String token) { // JWT 토큰에서 사용자 이름 추출
        Claims claims = parseClaims(token);
        return claims.get("username", String.class);
    }
    
    public Role getRole(String token) {
        Claims claims = parseClaims(token);
        String roleName = claims.get("role", String.class);
        return Role.valueOf(roleName);
    }
    
    private Claims parseClaims(String token) { // JWT 토큰에서 페이로드 추출
        return Jwts.parser()
                .verifyWith(getSigningKey()) //
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private SecretKey getSigningKey() { // JWT 서명에 사용할 비밀키 생성
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8); // JWT 서명에 사용할 비밀키를 바이트 배열로 변환
        return Keys.hmacShaKeyFor(keyBytes); // HMAC-SHA 알고리즘을 사용하여 비밀키 생성
    }
}
