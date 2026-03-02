package com.smartx.sales.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("sls_sales_detail")
public class SalesOrderDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long salesOrderId;  // 关联主单ID
    private Long materialId;    // 物料ID
    private Integer quantity;   // 销售数量
    private BigDecimal unitPrice; // 销售单价
    private BigDecimal totalPrice;// 明细总价
}
