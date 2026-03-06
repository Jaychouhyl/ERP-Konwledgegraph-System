package com.smartx.scm.controller;

import com.mybatisflex.core.paginate.Page;
import com.smartx.common.core.domain.Result;
import com.smartx.common.security.annotation.RequiresPermissions;
import com.smartx.scm.domain.entity.ScmInventory;
import com.smartx.scm.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scm/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    /**
     * 1. 核心业务接口：跨服务扣减库存 (原封不动保留你的心血)
     * 🔒 权限锁：只有拥有 'scm:inventory:deduct' 权限的人才能调用
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
     * 2. 新增：供前端页面展示的库存分页台账 (任务B)
     */
    @GetMapping("/page")
    // 列表查询权限，如果SQL未配可以先不加锁
    // @RequiresPermissions("scm:inventory:list")
    public Result<Page<ScmInventory>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "materialId", required = false) Long materialId) {

        return Result.success("库存台账查询成功", inventoryService.pageInventory(pageNum, pageSize, materialId));
    }
}