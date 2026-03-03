package com.smartx.api.inventory;

import com.smartx.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteInventoryService", value = "scm-service")
public interface RemoteInventoryService {

    @PostMapping("/scm/material/internal/deduct")
    Result<?> deductInventory(@RequestParam("materialId") Long materialId, 
                              @RequestParam("quantity") Integer quantity);
}
