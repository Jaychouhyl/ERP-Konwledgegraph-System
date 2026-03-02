package com.smartx.scm.controller;

import com.smartx.scm.domain.entity.BaseMaterial;
import com.smartx.scm.mapper.BaseMaterialMapper;
import com.smartx.scm.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/scm/material")
public class MaterialController {

    @Autowired
    private BaseMaterialMapper materialMapper;
    
    @Autowired
    private InventoryService inventoryService;

    // 1. 获取所有物料字典
    @GetMapping("/list")
    public Map<String, Object> listMaterials() {
        List<BaseMaterial> list = materialMapper.selectList(null);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        result.put("msg", "success");
        return result;
    }

    // 2. 供其他微服务(销售)调用的内部接口：扣减库存
    @PostMapping("/internal/deduct")
    public Map<String, Object> deductInventory(@RequestParam("materialId") Long materialId, 
                                               @RequestParam("quantity") Integer quantity) {
        inventoryService.deductInventory(materialId, quantity);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", true);
        result.put("msg", "扣减库存成功");
        return result;
    }
}
