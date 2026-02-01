package com.seongho.backend_core_lab.global.config;

import com.seongho.backend_core_lab.global.interceptor.AdminAuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
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
}
