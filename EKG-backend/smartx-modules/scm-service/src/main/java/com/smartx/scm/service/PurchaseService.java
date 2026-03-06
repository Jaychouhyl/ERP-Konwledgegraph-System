package com.smartx.scm.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.smartx.api.finance.RemoteFinanceService;
import com.smartx.common.core.domain.Result;
import com.smartx.common.security.context.UserContextHolder;

import com.smartx.scm.domain.entity.PurchaseOrderDetail;
import com.smartx.scm.domain.entity.ScmInventory;
import com.smartx.scm.domain.entity.PurchaseOrder;
import com.smartx.scm.mapper.PurchaseOrderDetailMapper;
import com.smartx.scm.mapper.PurchaseOrderMapper;
import com.smartx.scm.mapper.ScmInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mybatisflex.core.paginate.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.smartx.scm.domain.entity.table.ScmInventoryTableDef.SCM_INVENTORY;

/**
 * 🛒 采购订单核心业务服务
 */
@Service
public class PurchaseService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
    @Autowired
    private PurchaseOrderDetailMapper detailMapper;
    @Autowired
    private ScmInventoryMapper inventoryMapper;
    @Autowired
    private RemoteFinanceService remoteFinanceService;

    // ==================== 1. 核心保存逻辑 (打上安全与时间补丁) ====================
    @Transactional(rollbackFor = Exception.class)
    public String executePurchase(PurchaseOrder order, List<PurchaseOrderDetail> details) {

        order.setOrderNo("PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setAuditStatus("PASS"); // 预留给 RAG 的智能审核状态

        Long currentUserId = UserContextHolder.getUserId();
        if (currentUserId != null) {
            order.setCreateBy(currentUserId);
        }

        // 🌟 补丁2：手动补上丢失的灵魂时间
        order.setCreateTime(new java.util.Date());

        // 🌟 补丁1：后端强算总价防篡改
        BigDecimal totalAmount = java.math.BigDecimal.ZERO;
        for (PurchaseOrderDetail detail : details) {
            if (detail.getUnitPrice() != null && detail.getQuantity() != null) {
                // 后端强制计算：单价 × 数量
                BigDecimal tp = detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity()));
                detail.setTotalPrice(tp);
                totalAmount = totalAmount.add(tp);
            }
        }
        order.setTotalAmount(totalAmount);

        // 保存主表
        purchaseOrderMapper.insert(order);

        // 2. 遍历明细：保存明细表并增加对应物料的库存
        for (PurchaseOrderDetail detail : details) {
            detail.setPurchaseOrderId(order.getId());
            detailMapper.insert(detail);

            // 防死锁库存更新逻辑
            ScmInventory inventory = inventoryMapper.selectOneByQuery(
                    QueryWrapper.create().from(SCM_INVENTORY).where(SCM_INVENTORY.MATERIAL_ID.eq(detail.getMaterialId()))
            );

            if (inventory != null) {
                inventory.setCurrentQuantity(inventory.getCurrentQuantity() + detail.getQuantity());
                inventoryMapper.update(inventory);
            } else {
                inventory = new ScmInventory();
                inventory.setMaterialId(detail.getMaterialId());
                inventory.setCurrentQuantity(detail.getQuantity());
                inventoryMapper.insert(inventory);
            }
        }

        // 3. 跨服务调用：通知财务系统记录支出
        Result<?> financeResult = remoteFinanceService.recordFlow(
                "OUT", totalAmount, "PURCHASE", order.getId()
        );

        if (financeResult == null || financeResult.getCode() != 0) {
            throw new RuntimeException("财务付款失败，采购入库全局回滚！");
        }

        return "采购成功！单号：" + order.getOrderNo() + "，共入库 " + details.size() + " 种物料，财务已付款！";
    }

    // ==================== 2. 分页查询逻辑 (增加 supplierId 维度) ====================
    public Page<PurchaseOrder> pagePurchaseOrder(int pageNum, int pageSize, String orderNo, Long supplierId) {
        QueryWrapper query = QueryWrapper.create();

        // 按单号模糊查
        if (orderNo != null && !orderNo.isEmpty()) {
            query.where(PurchaseOrder::getOrderNo).like(orderNo);
        }
        // 🌟 补丁3：按供应商精准筛选 (老板和采购员极其需要的功能)
        if (supplierId != null) {
            query.where(PurchaseOrder::getSupplierId).eq(supplierId);
        }

        query.orderBy(PurchaseOrder::getId).desc();
        return purchaseOrderMapper.paginate(pageNum, pageSize, query);
    }

    /**
     * 根据主表ID，查询该订单下的所有物料明细
     */
    public List<PurchaseOrderDetail> getDetailsByOrderId(Long orderId) {
        return detailMapper.selectListByQuery(
                QueryWrapper.create().where(PurchaseOrderDetail::getPurchaseOrderId).eq(orderId)
        );
    }
}