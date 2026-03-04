package com.smartx.sales.application;

import com.smartx.api.finance.RemoteFinanceService;
import com.smartx.api.inventory.RemoteInventoryService;
import com.smartx.common.core.domain.Result;
import com.smartx.sales.domain.entity.SalesOrder;
import com.smartx.sales.mapper.SalesOrderMapper;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class CreateOrderService {

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    @Autowired
    private RemoteInventoryService remoteInventoryService;

    @Autowired
    private RemoteFinanceService remoteFinanceService;

    /**
     * 🌟 真实业务：创建销售单核心编排 (业财一体化)
     * @GlobalTransactional: 只要下面的任何一个微服务报错，立刻全局回滚！
     */
    @GlobalTransactional(name = "smartx-create-sales-order", rollbackFor = Exception.class)
    public Result<?> createSalesOrder(Long customerId, Long materialId, Integer qty, BigDecimal price) {
        
        // 1. 【本地事务】生成并保存销售单 (sales-service)
        SalesOrder order = new SalesOrder();
        order.setOrderNo("SO" + System.currentTimeMillis());
        order.setCustomerId(customerId);
        order.setTotalAmount(price.multiply(new BigDecimal(qty)));
        order.setStatus("CREATED"); // 修改为 String 类型，符合实体类定义
        order.setCreateTime(new Date());
        salesOrderMapper.insert(order);

        // 2. 【远程调用】扣减库存 (scm-service)
        Result<?> inventoryResult = remoteInventoryService.deductInventory(materialId, qty);
        if (inventoryResult.getCode() != 0) {
            // 如果库存不足，抛出异常，Seata 会自动把上面第 1 步插入的订单删掉！
            throw new RuntimeException("下单失败，库存微服务拦截：" + inventoryResult.getMsg());
        }

        // 3. 【远程调用】记录财务收款流水 (finance-service)
        // flowType = "IN" 表示收入
        Result<?> financeResult = remoteFinanceService.recordFlow("IN", order.getTotalAmount(), "SALES", order.getId());
        if (financeResult.getCode() != 0) {
            // 如果财务记账失败，抛出异常，Seata 会把扣掉的库存加回去，并把订单删掉！
            throw new RuntimeException("下单失败，财务微服务拦截：" + financeResult.getMsg());
        }

        return Result.success("销售单创建成功！(业务流：下单 -> 扣库存 -> 财务记账 一气呵成)");
    }
}
