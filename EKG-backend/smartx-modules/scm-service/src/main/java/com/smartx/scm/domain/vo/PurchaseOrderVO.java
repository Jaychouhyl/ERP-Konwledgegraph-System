package com.smartx.scm.domain.vo;

import com.smartx.scm.domain.entity.PurchaseOrder;
import com.smartx.scm.domain.entity.PurchaseOrderDetail;
import java.util.List;

/**
 * 📦 采购订单复合传参对象
 */
public class PurchaseOrderVO {
    private PurchaseOrder order;
    private List<PurchaseOrderDetail> details;

    public PurchaseOrder getOrder() { return order; }
    public void setOrder(PurchaseOrder order) { this.order = order; }

    public List<PurchaseOrderDetail> getDetails() { return details; }
    public void setDetails(List<PurchaseOrderDetail> details) { this.details = details; }
}