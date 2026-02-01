package com.seongho.backend_core_lab.global.config;

import com.seongho.backend_core_lab.global.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {
    
    private final AuthenticationFilter authenticationFilter;
    
    @Bean //Filter를 스프링 빈에 등록
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        /*
        @Component만 붙이면:
        → 자동 등록되지만 세부 설정 불가

        FilterRegistrationBean 사용:
        → URL 패턴, 순서 등 세밀한 제어 가능
         */


        registrationBean.setFilter(authenticationFilter); //AuthenticationFilter 등록
        
        registrationBean.addUrlPatterns("/*"); //모든 경로에 적용 - /auth/signup, /auth/login 경로는 인증 불필요, 이건 filter 내부에서 처리됨됨
        
        registrationBean.setOrder(1); //필터 순서 설정 , 1 - 가장 먼저 실행행
        //여러 필터가 있을때 실행 순서 지정, 숫자 작을수록 먼저 실행행

        registrationBean.setName("authenticationFilter"); //필터 이름 설정
        
        return registrationBean;
    }
}
