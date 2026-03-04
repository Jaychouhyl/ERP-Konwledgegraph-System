package com.smartx.common.core.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

/**
 * 🌟 OpenFeign 全局无损 Header 转发器
 */
@Configuration
public class FeignInterceptorConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    // ⚠️ 极其重要：跳过 content-length，否则 Feign 会因为长度计算不一致报 400 错
                    if ("content-length".equalsIgnoreCase(name) || "content-type".equalsIgnoreCase(name)) {
                        continue;
                    }
                    String values = request.getHeader(name);
                    template.header(name, values);
                }
            }
        }
    }
}