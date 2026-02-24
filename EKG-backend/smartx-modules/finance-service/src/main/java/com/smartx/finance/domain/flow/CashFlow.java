package com.smartx.finance.domain.flow;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CashFlow {
    private Long id;
    private BigDecimal amount;
    private String type; // INCOME or EXPENSE
    private String relatedOrderId;
}
