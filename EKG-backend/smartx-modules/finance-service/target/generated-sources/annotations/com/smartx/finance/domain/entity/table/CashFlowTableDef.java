package com.smartx.finance.domain.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class CashFlowTableDef extends TableDef {

    public static final CashFlowTableDef CASH_FLOW = new CashFlowTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn AMOUNT = new QueryColumn(this, "amount");

    public final QueryColumn FLOW_NO = new QueryColumn(this, "flow_no");

    public final QueryColumn FLOW_TYPE = new QueryColumn(this, "flow_type");

    public final QueryColumn BUSINESS_ID = new QueryColumn(this, "business_id");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn BUSINESS_TYPE = new QueryColumn(this, "business_type");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, AMOUNT, FLOW_NO, FLOW_TYPE, BUSINESS_ID, CREATE_TIME, BUSINESS_TYPE};

    public CashFlowTableDef() {
        super("", "fin_cash_flow");
    }

}
