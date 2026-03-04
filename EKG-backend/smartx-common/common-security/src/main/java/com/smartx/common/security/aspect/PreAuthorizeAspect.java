package com.smartx.common.security.aspect;

import com.smartx.common.redis.service.RedisService;
import com.smartx.common.security.annotation.RequiresPermissions;
import com.smartx.common.security.context.UserContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class PreAuthorizeAspect {

    @Autowired
    private RedisService redisService;

    // 拦截所有打上了 @RequiresPermissions 注解的方法
    @Around("@annotation(requiresPermissions)")
    public Object around(ProceedingJoinPoint point, RequiresPermissions requiresPermissions) throws Throwable {
        Long userId = UserContextHolder.getUserId();
        
        if (userId == null) {
            throw new RuntimeException("HTTP 401: 未登录或Token失效，请重新登录！");
        }
        
        // 🌟 上帝特权：超级管理员（ID为1）直接无条件放行！
        if (userId == 1L) {
            return point.proceed();
        }

        // 普通用户：去 Redis 里查他的专属权限抽屉
        String cacheKey = "login:perms:" + userId;
        List<String> userPerms = (List<String>) redisService.getCacheObject(cacheKey);

        // 如果 Redis 里没数据，或者他的权限列表里不包含这个接口需要的权限，直接拦截！
        String requiredPerm = requiresPermissions.value();
        if (userPerms == null || !userPerms.contains(requiredPerm)) {
            throw new RuntimeException("HTTP 403: 越权访问警告！您没有 [" + requiredPerm + "] 的操作权限！");
        }

        // 权限校验通过，执行原方法
        return point.proceed();
    }
}
