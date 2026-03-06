package com.smartx.sales.service;

import com.smartx.api.inventory.RemoteInventoryService;
import com.smartx.common.core.domain.Result;
import com.smartx.common.security.context.UserContextHolder;
import com.smartx.sales.domain.entity.SalesOrder;
import com.smartx.sales.domain.entity.SalesOrderDetail;
import com.smartx.sales.mapper.SalesOrderDetailMapper;
import com.smartx.sales.mapper.SalesOrderMapper;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 💰 销售订单核心业务服务
 */
@Service
public class OrderService {

    @Autowired
    private SalesOrderMapper salesOrderMapper;
    
    @Autowired
    private SalesOrderDetailMapper salesOrderDetailMapper;
    
    @Autowired
    private RemoteInventoryService remoteInventoryService; // 🌟 保留你调用 SCM 扣库存的逻辑
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate; // 🌟 完美找回你的 RocketMQ 异步解耦逻辑！

    @Transactional(rollbackFor = Exception.class)
    public String executeSales(SalesOrder order, List<SalesOrderDetail> details) {
        
        // 1. 生成基础订单数据
        order.setOrderNo("SO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus("CREATED"); // 1 代表待发货/已创建
        
        Long currentUserId = UserContextHolder.getUserId();
        if (currentUserId != null) {
            order.setCreateBy(currentUserId);
        }

        //  后端强算总价防篡改，并手动赋上创建时间
        BigDecimal totalAmount = java.math.BigDecimal.ZERO;
        for (SalesOrderDetail detail : details) {
            if (detail.getUnitPrice() != null && detail.getQuantity() != null) {
                // 后端强制计算：单价 × 数量
                BigDecimal tp = detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity()));
                detail.setTotalPrice(tp);
                totalAmount = totalAmount.add(tp);
            }
        }
        order.setTotalAmount(totalAmount);
        order.setCreateTime(new java.util.Date()); // 补上丢失的灵魂时间

        salesOrderMapper.insert(order);

        // 2. 循环保存明细 & 跨服务强同步扣减库存
        for (SalesOrderDetail detail : details) {
            detail.setSalesOrderId(order.getId());
            salesOrderDetailMapper.insert(detail);

            // 去 SCM 扣减真实的库存
            Result<?> inventoryResult = remoteInventoryService.deductInventory(detail.getMaterialId(), detail.getQuantity());
            
            if (inventoryResult == null || inventoryResult.getCode() != 0) {
                throw new RuntimeException("库存扣减失败（可能物料不足），销售单回滚！");
            }
        }

        // 3. 🌟 MQ 异步调用：发射消息给 finance-topic，通知财务记账 (IN)
        // 构造给 FinanceMessageListener 听的消息（根据你 finance 模块实际解析的 JSON 格式，这里发送标准报文）
        String financeMsg = String.format("{\"flowType\":\"IN\", \"amount\":%s, \"businessType\":\"SALES\", \"businessId\":%d}", 
                                          totalAmount.toString(), order.getId());
                                          
        rocketMQTemplate.convertAndSend("finance-topic", financeMsg);

        return "销售成功！单号：" + order.getOrderNo() + "，库存已同步扣减，已通过 MQ 异步通知财务入账！";
    }

    // ==================== 新增：销售单查询与分页 ====================

    // 增加 customerId 等多维度查询
    public Page<SalesOrder> pageSalesOrder(int pageNum, int pageSize, String orderNo, Long customerId) {
        QueryWrapper query = QueryWrapper.create();
        if (orderNo != null && !orderNo.isEmpty()) {
            query.where(SalesOrder::getOrderNo).like(orderNo);
        }
        if (customerId != null) {
            query.where(SalesOrder::getCustomerId).eq(customerId); // 允许按客户筛选
        }
        query.orderBy(SalesOrder::getId).desc();
        return salesOrderMapper.paginate(pageNum, pageSize, query);
    }
    /**
     * 根据销售主单ID，查询所有的售出商品明细
     */
    public List<SalesOrderDetail> getDetailsByOrderId(Long orderId) {
        return salesOrderDetailMapper.selectListByQuery(
                QueryWrapper.create().where(SalesOrderDetail::getSalesOrderId).eq(orderId)
        );
    }
}
