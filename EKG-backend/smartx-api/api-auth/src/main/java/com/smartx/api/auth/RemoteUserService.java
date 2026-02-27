package com.smartx.api.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserService", value = "auth-service")
public interface RemoteUserService {

    /**
     * 根据用户 ID 获取用户名 (用于各个微服务记录操作人)
     */
    @GetMapping("/auth/user/getName")
    String getUserNameById(@RequestParam("userId") Long userId);
}
