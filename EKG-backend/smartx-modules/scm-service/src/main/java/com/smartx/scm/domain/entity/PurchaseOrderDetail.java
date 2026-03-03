package com.smartx.scm.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Table("scm_purchase_detail")
public class PurchaseOrderDetail {
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private Long purchaseOrderId; // 关联采购单ID
    private Long materialId;      // 物料ID
    private Integer quantity;     // 采购数量
    private BigDecimal unitPrice; // 采购单价
    private BigDecimal totalPrice;// 明细总价
}
