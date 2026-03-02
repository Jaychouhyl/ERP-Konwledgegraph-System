package com.smartx.scm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartx.scm.api.FinanceFeignClient;
import com.smartx.scm.domain.entity.PurchaseOrder;
import com.smartx.scm.domain.entity.PurchaseOrderDetail;
import com.smartx.scm.domain.entity.ScmInventory;
import com.smartx.scm.mapper.PurchaseOrderDetailMapper;
import com.smartx.scm.mapper.PurchaseOrderMapper;
import com.smartx.scm.mapper.ScmInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
    @Autowired
    private PurchaseOrderDetailMapper detailMapper;
    @Autowired
    private ScmInventoryMapper inventoryMapper;
    @Autowired
    private FinanceFeignClient financeFeignClient;

    @Transactional(rollbackFor = Exception.class)
    public String executePurchase(Long materialId, Integer quantity, BigDecimal unitPrice) {
        
        // 1. 创建采购主单
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo("PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setSupplierId(1001L); // 假装是向台积电采购
        BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(quantity));
        order.setTotalAmount(totalAmount);
        order.setAuditStatus("PASS"); // 目前默认直接通过，以后这里会接入 RAG 智能审核！
        purchaseOrderMapper.insert(order);

        // 2. 创建明细
        PurchaseOrderDetail detail = new PurchaseOrderDetail();
        detail.setPurchaseOrderId(order.getId());
        detail.setMaterialId(materialId);
        detail.setQuantity(quantity);
        detail.setUnitPrice(unitPrice);
        detail.setTotalPrice(totalAmount);
        detailMapper.insert(detail);

        // 3. 入库：增加库存！
        ScmInventory inventory = inventoryMapper.selectOne(
                new LambdaQueryWrapper<ScmInventory>().eq(ScmInventory::getMaterialId, materialId)
        );
        if (inventory != null) {
            inventory.setCurrentQuantity(inventory.getCurrentQuantity() + quantity);
            inventoryMapper.updateById(inventory);
        }

        // 4. 🌟 跨服务调用：通知财务支出打款！
        Map<String, Object> financeResult = financeFeignClient.recordFlow(
                "OUT",           // 支出！
                totalAmount,     // 采购总金额
                "PURCHASE",      // 业务类型：采购
                order.getId()
        );

        if (financeResult == null || (Integer) financeResult.get("code") != 0) {
            throw new RuntimeException("财务付款失败，采购入库回滚！");
        }

        return "采购成功！单号：" + order.getOrderNo() + "，库存已增加，财务已付款！";
    }
}
