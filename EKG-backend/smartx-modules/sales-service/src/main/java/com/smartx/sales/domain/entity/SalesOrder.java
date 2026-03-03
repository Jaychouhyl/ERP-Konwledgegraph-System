package com.smartx.sales.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table("sls_sales_order")
public class SalesOrder {
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private String orderNo;       // 销售单号
    private Long customerId;      // 客户ID
    private BigDecimal totalAmount; // 总金额
    private String status;        // 状态 (CREATED/SHIPPED/COMPLETED)
    private Date createTime;
}
