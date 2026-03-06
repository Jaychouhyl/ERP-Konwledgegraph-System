package com.smartx.finance.controller;

import com.mybatisflex.core.paginate.Page;
import com.smartx.finance.domain.entity.CashFlow;
import com.smartx.finance.service.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smartx.common.core.domain.Result;
// import com.smartx.common.security.annotation.RequiresPermissions; // 预留权限注解

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/finance/flow")
public class FinanceFlowController {

    @Autowired
    private CashFlowService cashFlowService;

    // ====================================================================
    // 🌟 原有心血：供其他微服务调用的内部接口：记账 (绝对原汁原味保留)
    // ====================================================================
    @PostMapping("/internal/record")
    public Result<String> recordFlow(@RequestParam("flowType") String flowType,
                                     @RequestParam("amount") BigDecimal amount,
                                     @RequestParam("businessType") String businessType,
                                     @RequestParam("businessId") Long businessId) {
        String flowNo = cashFlowService.recordCashFlow(flowType, amount, businessType, businessId);
        return Result.success("资金流水记录成功", flowNo);
    }

    // ====================================================================
    // 🚀 新增接口 1：供前端页面展示的流水明细
    // ====================================================================
    @GetMapping("/page")
    // @RequiresPermissions("finance:flow:list") // 若后续加了菜单权限，可取消注释
    public Result<Page<CashFlow>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "flowType", required = false) String flowType,
            @RequestParam(value = "businessType", required = false) String businessType) {

        return Result.success("流水查询成功", cashFlowService.pageCashFlow(pageNum, pageSize, flowType, businessType));
    }

    // ====================================================================
    // 🚀 新增接口 2：供前端大盘调用的金库统计
    // ====================================================================
    @GetMapping("/dashboard/stats")
    public Result<Map<String, BigDecimal>> getDashboardStats() {
        return Result.success("财务大盘聚合成功", cashFlowService.getDashboardStats());
    }
}