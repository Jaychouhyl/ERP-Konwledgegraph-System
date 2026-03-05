package com.smartx.scm.controller;

import com.mybatisflex.core.paginate.Page;
import com.smartx.common.core.domain.Result;
import com.smartx.scm.domain.entity.BaseMaterial;
import com.smartx.scm.service.MaterialService;
import com.smartx.scm.service.InventoryService;
import com.smartx.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/scm/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private RedisService redisService;

    // 🌟 缓存 Key 前缀规范
    private static final String MATERIAL_CACHE_KEY = "scm:material:list";

    // 1. 获取所有物料字典（完美保留你的 Redis 缓存逻辑！）
    @GetMapping("/list")
    public Result<List<BaseMaterial>> listMaterials() {
        // 第一步：先去 Redis 翻抽屉
        List<BaseMaterial> cachedList = (List<BaseMaterial>) redisService.getCacheObject(MATERIAL_CACHE_KEY);
        if (cachedList != null) {
            return Result.success("【起飞】走 Redis 缓存查询成功！", cachedList);
        }

        // 第二步：抽屉里没有，调用 Service 去 MySQL 查
        List<BaseMaterial> list = materialService.listAll();

        // 第三步：查出来之后，放进抽屉里（缓存 2 小时）
        redisService.setCacheObject(MATERIAL_CACHE_KEY, list, 2, TimeUnit.HOURS);
        return Result.success("走 MySQL 数据库查询成功，并已存入缓存！", list);
    }

    // 2. 分页查询物料列表 (前端管理页面极其需要)
    @GetMapping("/page")
    public Result<Page<BaseMaterial>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return Result.success("分页查询成功", materialService.pageMaterial(pageNum, pageSize, keyword));
    }

    // 3. 查询单条详情
    @GetMapping("/{id}")
    public Result<BaseMaterial> getInfo(@PathVariable("id") Long id) {
        return Result.success("查询成功", materialService.getById(id));
    }

    // 4. 新增物料 (保留了你的写操作清空缓存)
    @PostMapping("/add")
    public Result<?> addMaterial(@RequestBody BaseMaterial material) {
        if (materialService.addMaterial(material)) {
            redisService.deleteObject(MATERIAL_CACHE_KEY);
            return Result.success("新增物料成功！", null);
        }
        return Result.fail("新增物料失败");
    }

    // 5. 修改物料信息
    @PutMapping("/update")
    public Result<?> updateMaterial(@RequestBody BaseMaterial material) {
        if (materialService.updateMaterial(material)) {
            redisService.deleteObject(MATERIAL_CACHE_KEY);
            return Result.success("修改物料成功！", null);
        }
        return Result.fail("修改物料失败");
    }

    // 6. 删除物料
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteMaterial(@PathVariable("id") Long id) {
        if (materialService.deleteMaterial(id)) {
            redisService.deleteObject(MATERIAL_CACHE_KEY);
            return Result.success("物料删除成功！", null);
        }
        return Result.fail("物料删除失败");
    }

    // 7. 供其他微服务(销售)调用的内部接口：扣减库存 (保留你的原有接口)
    @PostMapping("/internal/deduct")
    public Result<Boolean> deductInventory(@RequestParam("materialId") Long materialId,
                                           @RequestParam("quantity") Integer quantity) {
        inventoryService.deductInventory(materialId, quantity);
        return Result.success("扣减库存成功", true);
    }
}