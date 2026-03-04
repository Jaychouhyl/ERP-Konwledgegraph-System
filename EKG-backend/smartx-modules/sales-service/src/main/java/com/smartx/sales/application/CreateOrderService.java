package com.smartx.sales.application;

import com.smartx.api.inventory.RemoteInventoryService;
import com.smartx.common.core.domain.Result;
import com.smartx.sales.domain.entity.SalesOrder;
import com.smartx.sales.mapper.SalesOrderMapper;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.rocketmq.spring.core.RocketMQTemplate; // 🌟 引入 MQ 模板
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CreateOrderService {

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    @Autowired
    private RemoteInventoryService remoteInventoryService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate; // 🌟 注入火箭发射器！

    @GlobalTransactional(name = "smartx-create-sales-order", rollbackFor = Exception.class)
    public Result<?> createSalesOrder(Long customerId, Long materialId, Integer qty, BigDecimal price) {

        // 1. 【本地事务】生成并保存销售单
        SalesOrder order = new SalesOrder();
        order.setOrderNo("SO" + System.currentTimeMillis());
        order.setCustomerId(customerId);
        order.setTotalAmount(price.multiply(new BigDecimal(qty)));
        order.setStatus("1");
        order.setCreateTime(new Date());
        salesOrderMapper.insert(order);

        // 2. 【同步远程调用】扣减库存 (保证不超卖，必须同步阻塞)
        Result<?> inventoryResult = remoteInventoryService.deductInventory(materialId, qty);
        if (inventoryResult.getCode() != 0) {
            throw new RuntimeException("下单失败，库存不足：" + inventoryResult.getMsg());
        }

        // 3. 🌟【异步消息解耦】抛弃 OpenFeign，改用 RocketMQ 通知财务记账！
        Map<String, Object> msgBody = new HashMap<>();
        msgBody.put("orderNo", order.getOrderNo());
        msgBody.put("amount", order.getTotalAmount());
        msgBody.put("flowType", "IN");

        // 一行代码，发射消息到 "finance-topic" 主题！
        rocketMQTemplate.convertAndSend("finance-topic", msgBody);
        System.out.println("====== 🚀 订单创建成功！已向 MQ 发送财务记账异步消息 ======");

        return Result.success("销售单创建成功！系统正在后台为您处理财务账单...");
    }
}