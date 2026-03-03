package com.smartx.common.security.config;

import com.smartx.common.security.interceptor.UserInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityWebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 自动将拦截器注册到使用该组件的微服务中
        registry.addInterceptor(new UserInfoInterceptor()).addPathPatterns("/**");
    }
}
