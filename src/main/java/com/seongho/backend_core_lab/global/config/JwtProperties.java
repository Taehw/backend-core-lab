package com.seongho.backend_core_lab.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt") // application.properties 파일에서 jwt 접두사로 시작하는 프로퍼티를 자동으로 매핑
public class JwtProperties {
    
    private String secretKey; // JWT 서명에 사용할 비밀키
    
    private Long accessTokenExpiration; // Access Token 유효시간 (밀리초 단위)
    
    private Long refreshTokenExpiration; // Refresh Token 유효시간 (밀리초 단위)
}
