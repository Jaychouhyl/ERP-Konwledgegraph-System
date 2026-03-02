package com.smartx.finance.service;

import com.smartx.finance.domain.entity.CashFlow;
import com.smartx.finance.mapper.CashFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CashFlowService {

    @Autowired
    private CashFlowMapper cashFlowMapper;

    /**
     * 核心业务：记录资金流水 (提供给 Sales/SCM 服务通过 Feign 调用)
     */
    @Transactional(rollbackFor = Exception.class)
    public String recordCashFlow(String flowType, BigDecimal amount, String businessType, Long businessId) {
        CashFlow flow = new CashFlow();
        // 生成流水号，如 FIN-IN-A1B2C3
        flow.setFlowNo("FIN-" + flowType + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        flow.setFlowType(flowType);
        flow.setAmount(amount);
        flow.setBusinessType(businessType);
        flow.setBusinessId(businessId);
        
        cashFlowMapper.insert(flow);
        return flow.getFlowNo();
    }
}
