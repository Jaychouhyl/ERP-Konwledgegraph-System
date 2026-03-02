package com.smartx.scm.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("scm_purchase_detail")
public class PurchaseOrderDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long purchaseOrderId; // 关联采购单ID
    private Long materialId;      // 物料ID
    private Integer quantity;     // 采购数量
    private BigDecimal unitPrice; // 采购单价
    private BigDecimal totalPrice;// 明细总价
}
