package com.smartx.api.finance;

import com.smartx.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@FeignClient(contextId = "remoteFinanceService", value = "finance-service")
public interface RemoteFinanceService {

    /**
     * 真实业务：记录资金流水
     */
    @PostMapping("/finance/flow/internal/record")
    Result<?> recordFlow(@RequestParam("flowType") String flowType, 
                         @RequestParam("amount") BigDecimal amount, 
                         @RequestParam("businessType") String businessType, 
                         @RequestParam("businessId") Long businessId);
}
