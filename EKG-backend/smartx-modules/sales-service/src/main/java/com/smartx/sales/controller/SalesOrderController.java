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
        Map<String, Object> result = new HashMap<>();
        try {
            // 每次购买 1 台
            String msg = orderService.createOrderAndDeductInventory(productId, 1);
            result.put("code", 0);
            result.put("data", msg);
            result.put("msg", "success");
        } catch (Exception e) {
            // 捕获异常，比如库存不足，或者 Feign 调用失败
            result.put("code", 500);
            result.put("msg", e.getMessage());
        }
        return result;
    }
}
