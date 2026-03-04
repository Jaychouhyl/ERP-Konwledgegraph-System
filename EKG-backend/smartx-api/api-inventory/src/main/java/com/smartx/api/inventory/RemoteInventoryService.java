package com.smartx.api.inventory;

import com.smartx.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteInventoryService", value = "scm-service")
public interface RemoteInventoryService {
    
    /**
     * 真实业务：扣减库存
     */
    @PostMapping("/scm/inventory/deduct")
    Result<?> deductInventory(@RequestParam("materialId") Long materialId, @RequestParam("quantity") Integer quantity);
}
