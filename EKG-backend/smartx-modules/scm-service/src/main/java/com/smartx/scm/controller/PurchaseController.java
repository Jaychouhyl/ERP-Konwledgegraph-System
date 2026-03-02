package com.smartx.scm.controller;

import com.smartx.scm.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/scm/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/create")
    public Map<String, Object> createPurchase(@RequestParam("materialId") Long materialId,
                                              @RequestParam("quantity") Integer quantity) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 假设进货价是 3000 (之前销售价是 5999)
            String msg = purchaseService.executePurchase(materialId, quantity, new BigDecimal("3000.00"));
            result.put("code", 0);
            result.put("data", msg);
            result.put("msg", "success");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
        }
        return result;
    }
}
