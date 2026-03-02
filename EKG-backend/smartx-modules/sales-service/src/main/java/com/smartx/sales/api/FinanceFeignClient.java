package com.smartx.sales.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "finance-service")
public interface FinanceFeignClient {
    @PostMapping("/finance/flow/internal/record")
    Map<String, Object> recordFlow(@RequestParam("flowType") String flowType,
                                   @RequestParam("amount") BigDecimal amount,
                                   @RequestParam("businessType") String businessType,
                                   @RequestParam("businessId") Long businessId);
}
