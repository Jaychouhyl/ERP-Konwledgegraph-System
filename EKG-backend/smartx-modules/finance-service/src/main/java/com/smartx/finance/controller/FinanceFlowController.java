package com.smartx.finance.controller;

import com.smartx.finance.service.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/finance/flow")
public class FinanceFlowController {

    @Autowired
    private CashFlowService cashFlowService;

    // 供其他微服务调用的内部接口：记账
    @PostMapping("/internal/record")
    public Map<String, Object> recordFlow(@RequestParam("flowType") String flowType, 
                                          @RequestParam("amount") BigDecimal amount, 
                                          @RequestParam("businessType") String businessType, 
                                          @RequestParam("businessId") Long businessId) {
        String flowNo = cashFlowService.recordCashFlow(flowType, amount, businessType, businessId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", flowNo);
        result.put("msg", "资金流水记录成功");
        return result;
    }
}
