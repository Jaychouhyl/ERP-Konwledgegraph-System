package com.smartx.api.finance;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@FeignClient(contextId = "remoteFinanceService", value = "finance-service")
public interface RemoteFinanceService {

    /**
     * 记录资金流水 (比如销售收入、采购支出)
     * @param type 1: 收入 (销售), 2: 支出 (采购)
     * @param amount 金额
     */
    @PostMapping("/finance/flow/record")
    Boolean recordCashFlow(@RequestParam("type") Integer type, @RequestParam("amount") BigDecimal amount);
}
