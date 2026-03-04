package com.smartx.scm.controller;

import com.smartx.scm.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartx.common.core.domain.Result;
import com.smartx.common.security.annotation.RequiresPermissions;

import java.math.BigDecimal;

@RestController
@RequestMapping("/scm/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/create")
    @RequiresPermissions("scm:purchase:add") // 🌟 加锁：只有拥有这个权限标识的人才能调！
    public Result<String> createPurchase(@RequestParam("materialId") Long materialId,
                                         @RequestParam("quantity") Integer quantity) {
        // 假设进货价是 3000 (之前销售价是 5999)
        String msg = purchaseService.executePurchase(materialId, quantity, new BigDecimal("3000.00"));
        return Result.success(msg);
    }
}
