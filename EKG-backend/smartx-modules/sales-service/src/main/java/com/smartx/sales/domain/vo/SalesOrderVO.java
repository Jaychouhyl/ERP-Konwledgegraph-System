package com.smartx.sales.domain.vo;

import com.smartx.sales.domain.entity.SalesOrder;
import com.smartx.sales.domain.entity.SalesOrderDetail;
import java.util.List;

public class SalesOrderVO {
    private SalesOrder order;
    private List<SalesOrderDetail> details;

    public SalesOrder getOrder() { return order; }
    public void setOrder(SalesOrder order) { this.order = order; }

    public List<SalesOrderDetail> getDetails() { return details; }
    public void setDetails(List<SalesOrderDetail> details) { this.details = details; }
}
