package com.smartx.finance.application;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 🌟 财务系统 MQ 消费者
 * topic: 监听的主题 (必须和发送方一致)
 * consumerGroup: 消费者组名
 */
@Component
@RocketMQMessageListener(topic = "finance-topic", consumerGroup = "finance-service-group")
public class FinanceMessageListener implements RocketMQListener<Map<String, Object>> {

    // @Autowired
    // private CashFlowService cashFlowService; // 之后你可以把真实的记账逻辑写在这里

    @Override
    public void onMessage(Map<String, Object> message) {
        // 当 MQ 里有消息时，RocketMQ 会自动触发这个方法！
        System.out.println("=================================================");
        System.out.println("💰 财务服务收到异步消息，开始后台悄悄记账...");
        System.out.println("📦 订单号: " + message.get("orderNo"));
        System.out.println("💵 记账金额: " + message.get("amount"));
        System.out.println("=================================================");

        // cashFlowService.recordFlow(...); // 执行真实的数据库插入操作
    }
}