package com.smartx.sales.controller;

import com.smartx.sales.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sales/order")
public class SalesOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/create")
    public Map<String, Object> testCreateOrder(@RequestParam("productId") Long productId) {

        // 调用业务层，内部使用 Feign 调 SCM
        String result = orderService.createOrder(productId);

        // 🌟 核心魔法：包装成 Vben 5 严格要求的标准数据结构
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("code", 0);       // Vben 规定 0 代表业务成功
        responseMap.put("data", result);  // 真正的数据放在 data 里
        responseMap.put("message", "订单处理完毕");

        return responseMap;
    }
}