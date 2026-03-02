package com.smartx.scm.config;

import com.smartx.scm.interceptor.UserInfoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserInfoInterceptor userInfoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 让咱们的拦截器拦截 scm-service 下的所有请求
        registry.addInterceptor(userInfoInterceptor).addPathPatterns("/**");
    }
}
