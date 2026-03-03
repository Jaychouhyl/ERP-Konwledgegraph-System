package com.smartx.api.finance;

import com.smartx.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

// contextId 是为了防止多个 Bean 重名，value 是 Nacos 里的服务名
@FeignClient(contextId = "remoteFinanceService", value = "finance-service")
public interface RemoteFinanceService {
    
    // 🌟 注意：返回值已经换成了我们刚刚写好的全局 Result！
    @PostMapping("/finance/flow/internal/record")
    Result<?> recordFlow(@RequestParam("flowType") String flowType, 
                         @RequestParam("amount") BigDecimal amount, 
                         @RequestParam("businessType") String businessType, 
                         @RequestParam("businessId") Long businessId);
}
