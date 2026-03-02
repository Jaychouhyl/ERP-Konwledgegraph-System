package com.smartx.sales.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("sls_sales_order")
public class SalesOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;       // 销售单号
    private Long customerId;      // 客户ID
    private BigDecimal totalAmount; // 总金额
    private String status;        // 状态 (CREATED/SHIPPED/COMPLETED)
    private Date createTime;
}
