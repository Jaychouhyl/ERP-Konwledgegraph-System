package com.smartx.sales.controller;

import com.smartx.common.core.domain.Result;
import com.smartx.sales.application.CreateOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/sales")
public class SalesOrderController {

    @Autowired
    private CreateOrderService createOrderService;

    /**
     * 真实接口：提交销售单
     */
    @PostMapping("/create")
    public Result<?> createOrder(@RequestParam("customerId") Long customerId, 
                                 @RequestParam("materialId") Long materialId, 
                                 @RequestParam("qty") Integer qty, 
                                 @RequestParam("price") BigDecimal price) {
        return createOrderService.createSalesOrder(customerId, materialId, qty, price);
    }
}
