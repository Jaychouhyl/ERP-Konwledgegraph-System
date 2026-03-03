package com.smartx.scm.controller;

import com.smartx.scm.domain.entity.BaseMaterial;
import com.smartx.scm.mapper.BaseMaterialMapper;
import com.smartx.scm.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smartx.common.core.domain.Result;

import java.util.List;

import com.smartx.common.redis.service.RedisService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/scm/material")
public class MaterialController {

    @Autowired
    private BaseMaterialMapper materialMapper;
    
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private RedisService redisService;

    // 🌟 缓存 Key 前缀规范
    private static final String MATERIAL_CACHE_KEY = "scm:material:list";

    // 1. 获取所有物料字典（体验飞一般的速度）
    @GetMapping("/list")
    public Result<List<BaseMaterial>> listMaterials() {
        // 第一步：先去 Redis 翻抽屉
        List<BaseMaterial> cachedList = (List<BaseMaterial>) redisService.getCacheObject(MATERIAL_CACHE_KEY);
        if (cachedList != null) {
            return Result.success("【起飞】走 Redis 缓存查询成功！", cachedList);
        }

        // 第二步：抽屉里没有，老老实实去 MySQL 查
        List<BaseMaterial> list = materialMapper.selectList(null);

        // 第三步：查出来之后，放进抽屉里（缓存 2 小时）
        redisService.setCacheObject(MATERIAL_CACHE_KEY, list, 2, TimeUnit.HOURS);

        return Result.success("走 MySQL 数据库查询成功，并已存入缓存！", list);
    }

    // 2. 供其他微服务(销售)调用的内部接口：扣减库存
    @PostMapping("/internal/deduct")
    public Result<Boolean> deductInventory(@RequestParam("materialId") Long materialId, 
                                           @RequestParam("quantity") Integer quantity) {
        inventoryService.deductInventory(materialId, quantity);
        return Result.success("扣减库存成功", true);
    }

    // ========== 新增：物料的 CRUD ==========

    // 1. 新增物料 (@RequestBody 表示接收前端传来的 JSON 数据)
    @PostMapping("/add")
    public Result<Void> addMaterial(@RequestBody BaseMaterial material) {
        materialMapper.insert(material);
        // 🌟 核心：发生了写操作，立刻删除缓存！下次查询就会重新读 MySQL 并生成新缓存
        redisService.deleteObject(MATERIAL_CACHE_KEY);
        return Result.success("新增物料成功！", null);
    }

    // 2. 修改物料信息
    @PutMapping("/update")
    public Result<Void> updateMaterial(@RequestBody BaseMaterial material) {
        // MyBatis-Plus 会根据传进来的 id 自动去更新对应的非空字段
        materialMapper.updateById(material);
        redisService.deleteObject(MATERIAL_CACHE_KEY);
        return Result.success("修改物料成功！", null);
    }

    // 3. 删除物料 (真实场景下建议做逻辑删除，这里为了演示先用物理删除)
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteMaterial(@PathVariable("id") Long id) {
        materialMapper.deleteById(id);
        redisService.deleteObject(MATERIAL_CACHE_KEY);
        return Result.success("物料删除成功！", null);
    }
}
