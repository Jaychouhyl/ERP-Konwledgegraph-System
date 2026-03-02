package com.smartx.scm.interceptor;

import com.smartx.scm.context.UserContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 🌟 核心：从网关透传的 Header 中拿出 X-User-Id
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr != null && !userIdStr.isEmpty()) {
            // 存入当前线程的口袋中
            UserContextHolder.setUserId(Long.parseLong(userIdStr));
        }
        return true; // 放行请求，继续往下走去 Controller
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后，必须清理口袋，防止内存泄漏和线程池数据串型！
        UserContextHolder.clear();
    }
}
