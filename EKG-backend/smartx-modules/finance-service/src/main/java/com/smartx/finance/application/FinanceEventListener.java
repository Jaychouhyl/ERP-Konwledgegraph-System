package com.smartx.finance.application;

import org.springframework.stereotype.Service;

@Service
public class FinanceEventListener {
    // Listen to RocketMQ events here
    public void handlePaymentEvent(String event) {
        System.out.println("Received payment event: " + event);
    }
}
