package com.smartx.scm.domain.purchase.repository;

import com.smartx.scm.domain.purchase.entity.PurchaseOrder;

public interface IPurchaseRepository {
    void save(PurchaseOrder order);
    PurchaseOrder findById(String id);
}
