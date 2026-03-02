package com.smartx.scm.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("scm_purchase_order")
public class PurchaseOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long supplierId;     // 供应商ID
    private BigDecimal totalAmount;
    private String auditStatus;  // 审核状态 PENDING/PASS/REJECT
    private String auditSuggestion;
    private Long createBy;
    private Date createTime;
}
