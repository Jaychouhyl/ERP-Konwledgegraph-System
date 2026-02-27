package com.smartx.scm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scm/inventory")
public class InventoryController {

    /**
     * 提供给 sales-service 调用的 Feign 接口
     */
    @GetMapping("/check")
    public Boolean checkStock(@RequestParam("productId") Long productId) {
        System.out.println("====== 收到来自 Sales 的库存检查请求，商品ID：" + productId + " ======");
        
        // 模拟数据库查询逻辑：假设商品ID为 1 有货，其他都没货
        if (productId != null && productId == 1L) {
            return true;
        }
        return false;
    }
}
