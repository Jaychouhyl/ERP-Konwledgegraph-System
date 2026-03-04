package com.smartx.scm.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.smartx.scm.domain.entity.ScmInventory;
import com.smartx.scm.mapper.ScmInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.smartx.scm.domain.entity.table.ScmInventoryTableDef.SCM_INVENTORY;

@Service
public class InventoryService {

    @Autowired
    private ScmInventoryMapper inventoryMapper;

    /**
     * 真实的库存扣减逻辑 (企业级应用)
     * @param materialId 物料ID
     * @param deductQuantity 扣减数量
     * @return 是否扣减成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deductInventory(Long materialId, Integer deductQuantity) {
        // 1. 查询当前库存记录
        ScmInventory inventory = inventoryMapper.selectOneByQuery(
                QueryWrapper.create().from(SCM_INVENTORY).where(SCM_INVENTORY.MATERIAL_ID.eq(materialId))
        );

        // 2. 校验库存是否存在，以及库存容量是否足够
        if (inventory == null || inventory.getCurrentQuantity() < deductQuantity) {
            return false; // 库存不足或物料不存在
        }

        // 3. 执行真实扣减 (底层更新 SQL)
        inventory.setCurrentQuantity(inventory.getCurrentQuantity() - deductQuantity);
        int rows = inventoryMapper.update(inventory);
        
        return rows > 0;
    }
}
