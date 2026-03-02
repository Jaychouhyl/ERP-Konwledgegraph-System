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

    // ========== 新增：物料的 CRUD ==========

    // 1. 新增物料 (@RequestBody 表示接收前端传来的 JSON 数据)
    @PostMapping("/add")
    public Map<String, Object> addMaterial(@RequestBody BaseMaterial material) {
        materialMapper.insert(material);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "新增物料成功！");
        return result;
    }

    // 2. 修改物料信息
    @PutMapping("/update")
    public Map<String, Object> updateMaterial(@RequestBody BaseMaterial material) {
        // MyBatis-Plus 会根据传进来的 id 自动去更新对应的非空字段
        materialMapper.updateById(material);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "修改物料成功！");
        return result;
    }

    // 3. 删除物料 (真实场景下建议做逻辑删除，这里为了演示先用物理删除)
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteMaterial(@PathVariable("id") Long id) {
        materialMapper.deleteById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "物料删除成功！");
        return result;
    }
}
