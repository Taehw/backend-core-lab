package com.seongho.backend_core_lab.global.config;

import com.seongho.backend_core_lab.global.interceptor.AdminAuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer { 
//WebMVvcConfigurer 인터페이스 구현 -> 스프링 MVC 설정 커스터마이징 가능
//Interceptor, CORS, 메시지 컨버터등 설정 가능
    private final AdminAuthorizationInterceptor adminAuthorizationInterceptor; //관리자 권한 인터셉터 등록
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthorizationInterceptor) //관리자 권한 인터셉터 등록
                .addPathPatterns("/admin/**"); //관리자 권한 인터셉터 적용 경로 설정
    } //.excludePathPatterns("/auth/**"); //인증 불필요 경로 설정
    //인증 불필요 경로는 filter에서 처리됨
    //이건 interceptor에서 처리됨
    //이건 메시지 컨버터에서 처리됨

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:3000",      // Vite 커스텀 포트
                    "http://localhost:5173",      // Vite 기본 포트
                    "https://your-ngrok-url.ngrok-free.app"  // ngrok URL (필요시 수정)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600)  // preflight 요청 캐시 시간 (초)
                .exposedHeaders("Authorization", "Content-Type");  // 프론트엔드에서 접근 가능한 헤더
    }
}
