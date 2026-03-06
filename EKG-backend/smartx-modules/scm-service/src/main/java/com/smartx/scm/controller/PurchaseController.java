package com.smartx.scm.controller;

import com.smartx.scm.service.PurchaseService;
import com.smartx.scm.domain.vo.PurchaseOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smartx.common.core.domain.Result;
import com.smartx.common.security.annotation.RequiresPermissions;

import com.mybatisflex.core.paginate.Page;
import com.smartx.scm.domain.entity.PurchaseOrder;
import com.smartx.scm.domain.entity.PurchaseOrderDetail;
import java.util.List;

/**
 * 🛒 采购业务对外接口
 */
@RestController
@RequestMapping("/scm/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    /**
     * 接收前端 JSON 一键创建采购单
     */
    @PostMapping("/create")
    @RequiresPermissions("scm:purchase:add") // 你原本的权限锁完整保留
    public Result<String> createPurchase(@RequestBody PurchaseOrderVO vo) {

        // 参数非空校验
        if (vo.getOrder() == null || vo.getDetails() == null || vo.getDetails().isEmpty()) {
            return Result.fail("采购单主表或明细数据不能为空！");
        }

        // 核心一键触发：主子表保存 -> 循环库存累加 -> 跨微服务财务打款
        String msg = purchaseService.executePurchase(vo.getOrder(), vo.getDetails());
        return Result.success(msg);
    }

    /**
     * 分页查询采购主单 (带权限锁)
     */
    @GetMapping("/page")
    @RequiresPermissions("scm:purchase:list") // 🌟 你的权限锁，稳如泰山！
    public Result<Page<PurchaseOrder>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderNo", required = false) String orderNo,
            @RequestParam(value = "supplierId", required = false) Long supplierId) { // 🌟 增加供应商入参

        return Result.success("分页查询成功", purchaseService.pagePurchaseOrder(pageNum, pageSize, orderNo, supplierId));
    }

    @GetMapping("/detail/{id}")
    @RequiresPermissions("scm:purchase:list")
    public Result<List<PurchaseOrderDetail>> getDetail(@PathVariable("id") Long id) {
        return Result.success("明细查询成功", purchaseService.getDetailsByOrderId(id));
    }
}