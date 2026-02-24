package com.smartx.scm.domain.purchase.entity;

import lombok.Data;

@Data
public class PurchaseOrder {
    private String id;
    private String product;
    private int quantity;
}
