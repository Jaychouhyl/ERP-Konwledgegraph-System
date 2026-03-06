package com.smartx.sales.controller;

// 🌟 注意这里：必须是 MyBatis-Flex 的 Page
import com.mybatisflex.core.paginate.Page;
import com.smartx.common.core.domain.Result;
import com.smartx.common.security.annotation.RequiresPermissions;
import com.smartx.sales.domain.entity.SalesOrder;
import com.smartx.sales.domain.entity.SalesOrderDetail;
import com.smartx.sales.domain.vo.SalesOrderVO;
import com.smartx.sales.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 💰 销售出库流对外接口
 */
@RestController
@RequestMapping("/sales/order")
public class SalesOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 1. 一键创建销售单 (带权限锁)
     */
    @PostMapping("/create")
    @RequiresPermissions("sales:order:add")
    public Result<String> createOrder(@RequestBody SalesOrderVO vo) {
        if (vo.getOrder() == null || vo.getDetails() == null || vo.getDetails().isEmpty()) {
            return Result.fail("销售单主表或明细不能为空！");
        }
        String msg = orderService.executeSales(vo.getOrder(), vo.getDetails());
        return Result.success(msg);
    }

    /**
     * 2. 分页查询销售主单 (带权限锁)
     */
    @GetMapping("/page")
    @RequiresPermissions("sales:order:list")
    public Result<Page<SalesOrder>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderNo", required = false) String orderNo,
            @RequestParam(value = "customerId", required = false) Long customerId) { // 🌟 加上这个

        return Result.success("分页查询成功", orderService.pageSalesOrder(pageNum, pageSize, orderNo, customerId));
    }

    /**
     * 3. 查询单据的物料明细 (带权限锁)
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("sales:order:list")
    public Result<List<SalesOrderDetail>> getDetail(@PathVariable("id") Long id) {
        return Result.success("明细查询成功", orderService.getDetailsByOrderId(id));
    }
}