package com.smartx.sales.controller;

import com.smartx.sales.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales/order")
public class SalesOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/create")
    public String testCreateOrder(@RequestParam("productId") Long productId) {
        // 调用 Service，Service 内部会通过 Feign 去调 SCM
        return orderService.createOrder(productId);
    }
}
