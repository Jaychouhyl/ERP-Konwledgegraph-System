package com.smartx.sales.service;

import com.smartx.api.inventory.RemoteInventoryService;
import com.smartx.api.finance.RemoteFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class OrderService {

    @Autowired
    private RemoteInventoryService remoteInventoryService;

    @Autowired
    private RemoteFinanceService remoteFinanceService;

    public String createOrder(Long productId) {
        // 1. 查库存
        Boolean hasStock = remoteInventoryService.checkStock(productId);
        
        if (hasStock != null && hasStock) {
            // 2. 库存充足，调用财务服务记录收入 (假设卖了 5000 块)
            remoteFinanceService.recordCashFlow(1, new BigDecimal("5000.00"));
            return "商品库存充足，财务已入账，订单创建成功！";
        } else {
            return "商品库存不足，订单创建失败！";
        }
    }
}
