package com.smartx.sales.service;

import com.smartx.api.inventory.RemoteInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    // 🌟 直接注入刚才在 api-inventory 写的接口
    @Autowired
    private RemoteInventoryService remoteInventoryService;

    public String createOrder(Long productId) {
        // 发起远程调用
        Boolean hasStock = remoteInventoryService.checkStock(productId);
        
        if (hasStock != null && hasStock) {
            return "商品库存充足，订单创建成功！";
        } else {
            return "商品库存不足，订单创建失败！";
        }
    }
}
