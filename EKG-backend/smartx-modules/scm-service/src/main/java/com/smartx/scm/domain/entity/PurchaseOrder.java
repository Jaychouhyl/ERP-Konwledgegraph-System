package com.smartx.scm.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table("scm_purchase_order")
public class PurchaseOrder {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private String orderNo;
    private Long supplierId;     // 供应商ID
    private BigDecimal totalAmount;
    private String auditStatus;  // 审核状态 PENDING/PASS/REJECT
    private String auditSuggestion;
    private Long createBy;
    private Date createTime;
}
