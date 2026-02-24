package com.smartx.sales.domain.order;

import lombok.Data;
import java.util.List;

@Data
public class SalesOrder {
    private String orderId;
    private String customerId;
    private List<OrderItem> items;
}
