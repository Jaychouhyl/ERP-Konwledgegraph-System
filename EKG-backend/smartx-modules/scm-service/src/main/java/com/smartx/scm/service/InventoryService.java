package com.smartx.scm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartx.scm.domain.entity.ScmInventory;
import com.smartx.scm.mapper.ScmInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    @Autowired
    private ScmInventoryMapper inventoryMapper;

    /**
     * 核心业务：扣减库存 (提供给 Sales 服务通过 Feign 调用)
     * @param materialId 物料ID
     * @param quantity 扣减数量
     * @return 是否扣减成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deductInventory(Long materialId, Integer quantity) {
        ScmInventory inventory = inventoryMapper.selectOne(
                new LambdaQueryWrapper<ScmInventory>().eq(ScmInventory::getMaterialId, materialId)
        );

        if (inventory == null || inventory.getCurrentQuantity() < quantity) {
            throw new RuntimeException("库存不足，无法完成订单！当前库存: " +
                    (inventory == null ? 0 : inventory.getCurrentQuantity()));
        }

        // 扣减库存
        inventory.setCurrentQuantity(inventory.getCurrentQuantity() - quantity);
        inventoryMapper.updateById(inventory);
        
        // 此处未来可扩展：如果扣减后 currentQuantity < safeQuantity，发送消息给 RAG 进行采购预警
        
        return true;
    }
}
