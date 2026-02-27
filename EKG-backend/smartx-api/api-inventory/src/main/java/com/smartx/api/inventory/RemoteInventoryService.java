package com.smartx.api.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// value = "scm-service" 指向被调用的微服务在 Nacos 中的名字
// contextId 用于防止多个 Feign 接口同名冲突
@FeignClient(contextId = "remoteInventoryService", value = "scm-service")
public interface RemoteInventoryService {

    /**
     * 检查库存是否充足
     */
    @GetMapping("/api/scm/inventory/check")
    Boolean checkStock(@RequestParam("productId") Long productId);
}
