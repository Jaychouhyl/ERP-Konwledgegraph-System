package com.smartx.finance.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.smartx.common.security.context.UserContextHolder;
import com.smartx.finance.domain.entity.CashFlow;
import com.smartx.finance.mapper.CashFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// 导入 MyBatis-Flex 常量，用于优雅查询
import static com.smartx.finance.domain.entity.table.CashFlowTableDef.CASH_FLOW;

@Service
public class CashFlowService {

    @Autowired
    private CashFlowMapper cashFlowMapper;

    // ====================================================================
    // 🌟 原有心血：核心业务记录资金流水 (完全保留你的原始逻辑与返回值)
    // ====================================================================
    @Transactional(rollbackFor = Exception.class)
    public String recordCashFlow(String flowType, BigDecimal amount, String businessType, Long businessId) {
        CashFlow flow = new CashFlow();
        // 生成流水号，如 FIN-IN-A1B2C3
        flow.setFlowNo("FIN-" + flowType + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        flow.setFlowType(flowType);
        flow.setAmount(amount);
        flow.setBusinessType(businessType);
        flow.setBusinessId(businessId);

        // 🌟 补丁：增加时间与操作人 (做 try-catch 是因为 MQ 异步触发时没有登录 token)
        flow.setCreateTime(new Date());
        try {
            Long currentUserId = UserContextHolder.getUserId();
            if (currentUserId != null) {
                flow.setCreateBy(currentUserId);
            }
        } catch (Exception e) {
            // 异步线程静默处理
        }

        cashFlowMapper.insert(flow);
        return flow.getFlowNo();
    }

    // ====================================================================
    // 🚀 新增能力 1：前端财务明细表 (分页多维查询)
    // ====================================================================
    public Page<CashFlow> pageCashFlow(int pageNum, int pageSize, String flowType, String businessType) {
        QueryWrapper query = QueryWrapper.create();

        if (flowType != null && !flowType.isEmpty()) {
            query.where(CASH_FLOW.FLOW_TYPE.eq(flowType));
        }
        if (businessType != null && !businessType.isEmpty()) {
            query.where(CASH_FLOW.BUSINESS_TYPE.eq(businessType));
        }

        query.orderBy(CASH_FLOW.ID.desc());
        return cashFlowMapper.paginate(pageNum, pageSize, query);
    }

    // ====================================================================
    // 🚀 新增能力 2：Dashboard 聚合统计引擎
    // ====================================================================
    public Map<String, BigDecimal> getDashboardStats() {
        Map<String, BigDecimal> stats = new HashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        List<CashFlow> allFlows = cashFlowMapper.selectAll();

        for (CashFlow f : allFlows) {
            if ("IN".equalsIgnoreCase(f.getFlowType()) && f.getAmount() != null) {
                totalIncome = totalIncome.add(f.getAmount());
            } else if ("OUT".equalsIgnoreCase(f.getFlowType()) && f.getAmount() != null) {
                totalExpense = totalExpense.add(f.getAmount());
            }
        }

        stats.put("totalIncome", totalIncome);
        stats.put("totalExpense", totalExpense);
        stats.put("netProfit", totalIncome.subtract(totalExpense));

        return stats;
    }
}