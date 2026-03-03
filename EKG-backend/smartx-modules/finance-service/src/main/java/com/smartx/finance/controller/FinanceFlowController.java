package com.smartx.finance.controller;

import com.smartx.finance.service.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smartx.common.core.domain.Result;

import java.math.BigDecimal;

@RestController
@RequestMapping("/finance/flow")
public class FinanceFlowController {

    @Autowired
    private CashFlowService cashFlowService;

    // 供其他微服务调用的内部接口：记账
    @PostMapping("/internal/record")
    public Result<String> recordFlow(@RequestParam("flowType") String flowType, 
                                     @RequestParam("amount") BigDecimal amount, 
                                     @RequestParam("businessType") String businessType, 
                                     @RequestParam("businessId") Long businessId) {
        String flowNo = cashFlowService.recordCashFlow(flowType, amount, businessType, businessId);
        return Result.success("资金流水记录成功", flowNo);
    }
}
