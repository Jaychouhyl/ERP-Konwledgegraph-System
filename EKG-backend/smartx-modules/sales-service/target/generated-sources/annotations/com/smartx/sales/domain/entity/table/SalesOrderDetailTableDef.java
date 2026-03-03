package com.smartx.sales.domain.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SalesOrderDetailTableDef extends TableDef {

    public static final SalesOrderDetailTableDef SALES_ORDER_DETAIL = new SalesOrderDetailTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn QUANTITY = new QueryColumn(this, "quantity");

    public final QueryColumn UNIT_PRICE = new QueryColumn(this, "unit_price");

    public final QueryColumn MATERIAL_ID = new QueryColumn(this, "material_id");

    public final QueryColumn TOTAL_PRICE = new QueryColumn(this, "total_price");

    public final QueryColumn SALES_ORDER_ID = new QueryColumn(this, "sales_order_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, QUANTITY, UNIT_PRICE, MATERIAL_ID, TOTAL_PRICE, SALES_ORDER_ID};

    public SalesOrderDetailTableDef() {
        super("", "sls_sales_detail");
    }

}
