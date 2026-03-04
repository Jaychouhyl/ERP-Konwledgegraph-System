package com.smartx.scm.controller;

import com.smartx.common.core.domain.Result;
import com.smartx.common.security.annotation.RequiresPermissions;
import com.smartx.scm.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scm/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    /**
     * 核心业务接口：扣减库存
     * 🔒 权限锁：只有拥有 'scm:inventory:deduct' 权限的人才能调用此接口！
     */
    @PostMapping("/deduct")
    @RequiresPermissions("scm:inventory:deduct")
    public Result<String> deductInventory(@RequestParam("materialId") Long materialId, 
                                          @RequestParam("quantity") Integer quantity) {
        // 基础参数校验
        if (quantity == null || quantity <= 0) {
            return Result.fail(400, "扣减数量必须大于0");
        }
        
        // 调用 Service 层执行真实扣减
        boolean success = inventoryService.deductInventory(materialId, quantity);
        
        if (success) {
            return Result.success("库存扣减成功！", null);
        } else {
            return Result.fail(500, "库存不足或物料不存在，扣减失败！");
        }
    }

    /**
     * 提供给 sales-service 调用的 Feign 接口 (保留旧接口以防兼容性问题，建议后续废弃)
     */
    @GetMapping("/check")
    public Boolean checkStock(@RequestParam("productId") Long productId) {
        // ... 旧逻辑保留或重构 ...
        return true; 
    }
}
