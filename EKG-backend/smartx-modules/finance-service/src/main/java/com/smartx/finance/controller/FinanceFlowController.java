package com.smartx.finance.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController
@RequestMapping("/finance/flow") // 注意不要加 /api
public class FinanceFlowController {

    @PostMapping("/record")
    public Boolean recordCashFlow(@RequestParam("type") Integer type, @RequestParam("amount") BigDecimal amount) {
        String typeName = type == 1 ? "收入" : "支出";
        System.out.println("====== 财务服务收到记账请求：[" + typeName + "] 金额：" + amount + " ======");
        return true;
    }
}
