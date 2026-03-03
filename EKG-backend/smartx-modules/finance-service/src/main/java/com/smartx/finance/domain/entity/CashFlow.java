package com.smartx.finance.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table("fin_cash_flow")
public class CashFlow {
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private String flowNo;       // 流水号
    private String flowType;     // 流向 (IN收入 / OUT支出)
    private BigDecimal amount;   // 金额
    private String businessType; // 业务类型 (PURCHASE采购付款 / SALES销售收款)
    private Long businessId;     // 关联的业务单据ID (销售单ID等)
    private Date createTime;
}
