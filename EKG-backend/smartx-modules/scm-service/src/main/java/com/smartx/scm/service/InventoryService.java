package com.smartx.scm.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.smartx.scm.domain.entity.ScmInventory;
import com.smartx.scm.mapper.ScmInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 📦 库存核心业务服务
 */
@Service
public class InventoryService {

    @Autowired
    private ScmInventoryMapper inventoryMapper;

    // =========================================================
    // 🌟 原有心血：供内部微服务调用的库存扣减逻辑 (一字不差保留)
    // =========================================================
    @Transactional(rollbackFor = Exception.class)
    public boolean deductInventory(Long materialId, Integer quantity) {
        ScmInventory inventory = inventoryMapper.selectOneByQuery(
                QueryWrapper.create().where(ScmInventory::getMaterialId).eq(materialId)
        );

        if (inventory != null && inventory.getCurrentQuantity() >= quantity) {
            inventory.setCurrentQuantity(inventory.getCurrentQuantity() - quantity);
            return inventoryMapper.update(inventory) > 0;
        }
        return false;
    }

    // =========================================================
    // 🚀 新增能力：供前端展示的库存台账分页查询
    // =========================================================
    public Page<ScmInventory> pageInventory(int pageNum, int pageSize, Long materialId) {
        QueryWrapper query = QueryWrapper.create();

        // 如果前端在搜索框传入了具体物料ID，就精准查询
        if (materialId != null) {
            query.where(ScmInventory::getMaterialId).eq(materialId);
        }

        // 按最后更新时间或 ID 倒序排列
        query.orderBy(ScmInventory::getId).desc();

        return inventoryMapper.paginate(pageNum, pageSize, query);
    }
}