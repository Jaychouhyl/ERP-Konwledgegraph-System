package com.smartx.sales.domain.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SalesOrderTableDef extends TableDef {

    public static final SalesOrderTableDef SALES_ORDER = new SalesOrderTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn ORDER_NO = new QueryColumn(this, "order_no");

    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn CUSTOMER_ID = new QueryColumn(this, "customer_id");

    public final QueryColumn TOTAL_AMOUNT = new QueryColumn(this, "total_amount");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, STATUS, ORDER_NO, CREATE_BY, CREATE_TIME, CUSTOMER_ID, TOTAL_AMOUNT};

    public SalesOrderTableDef() {
        super("", "sls_sales_order");
    }

}
