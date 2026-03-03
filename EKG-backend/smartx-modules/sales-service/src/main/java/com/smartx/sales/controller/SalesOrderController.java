package com.smartx.sales.controller;

import com.smartx.sales.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartx.common.core.domain.Result;

@RestController
@RequestMapping("/sales/order")
public class SalesOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/create")
    public Result<String> testCreateOrder(@RequestParam("productId") Long productId) {
        // 每次购买 1 台
        String msg = orderService.createOrderAndDeductInventory(productId, 1);
        return Result.success(msg);
    }
}
