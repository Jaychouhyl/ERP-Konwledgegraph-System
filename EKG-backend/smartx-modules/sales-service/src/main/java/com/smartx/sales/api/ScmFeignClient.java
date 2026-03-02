package com.smartx.sales.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

// 🌟 指定要调用的微服务在 Nacos 中的名称
@FeignClient(name = "scm-service")
public interface ScmFeignClient {

    // 🌟 这里的路径和参数必须和之前 scm-service 里写的 Controller 接口一模一样！
    @PostMapping("/scm/material/internal/deduct")
    Map<String, Object> deductInventory(@RequestParam("materialId") Long materialId,
                                        @RequestParam("quantity") Integer quantity);
}
