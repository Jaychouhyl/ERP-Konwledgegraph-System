package com.smartx.sales.service;

import com.smartx.sales.api.FinanceFeignClient;
import com.smartx.sales.api.ScmFeignClient;
import com.smartx.sales.domain.entity.SalesOrder;
import com.smartx.sales.domain.entity.SalesOrderDetail;
import com.smartx.sales.mapper.SalesOrderDetailMapper;
import com.smartx.sales.mapper.SalesOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private SalesOrderMapper orderMapper;

    @Autowired
    private SalesOrderDetailMapper orderDetailMapper;

    @Autowired
    private ScmFeignClient scmFeignClient; // 🌟 注入我们刚刚写的这把“倚天剑”

    @Autowired
    private FinanceFeignClient financeFeignClient; // 🌟 注入第二把“倚天剑”

    @Transactional(rollbackFor = Exception.class)
    public String createOrderAndDeductInventory(Long productId, Integer quantity) {
        
        // 1. 🌟 跨服务调用：去 SCM 扣减库存
        // 假设你要卖 1 台手机
        Map<String, Object> feignResult = scmFeignClient.deductInventory(productId, quantity);
        
        // 检查 Feign 调用是否成功 (判断 code 是否为 0)
        if (feignResult == null || (Integer) feignResult.get("code") != 0) {
            throw new RuntimeException("跨服务扣减库存失败！原因：" + 
                    (feignResult != null ? feignResult.get("msg") : "网络异常"));
        }

        // 2. 扣减成功，开始在本地创建销售订单主表
        SalesOrder order = new SalesOrder();
        order.setOrderNo("SO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomerId(1L); // 假装是客户1来买的
        
        // 假设一台手机单价 5999
        BigDecimal unitPrice = new BigDecimal("5999.00");
        BigDecimal totalPrice = unitPrice.multiply(new BigDecimal(quantity));
        order.setTotalAmount(totalPrice);
        order.setStatus("CREATED");
        orderMapper.insert(order);

        // 3. 创建订单明细表
        SalesOrderDetail detail = new SalesOrderDetail();
        detail.setSalesOrderId(order.getId());
        detail.setMaterialId(productId);
        detail.setQuantity(quantity);
        detail.setUnitPrice(unitPrice);
        detail.setTotalPrice(totalPrice);
        orderDetailMapper.insert(detail);

        // 4. 🌟 跨服务调用二：通知财务中心入账！
        Map<String, Object> financeResult = financeFeignClient.recordFlow(
                "IN",            // 收入
                totalPrice,      // 订单总金额
                "SALES",         // 业务类型：销售
                order.getId()    // 刚生成的订单ID
        );

        if (financeResult == null || (Integer) financeResult.get("code") != 0) {
            throw new RuntimeException("财务核算失败！入账异常取消交易。");
        }

        return "订单生成成功！单号：" + order.getOrderNo() + "，财务已入账！";
    }
}
