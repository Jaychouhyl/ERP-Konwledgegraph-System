package com.smartx.scm.domain.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class PurchaseOrderTableDef extends TableDef {

    public static final PurchaseOrderTableDef PURCHASE_ORDER = new PurchaseOrderTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn ORDER_NO = new QueryColumn(this, "order_no");

    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn SUPPLIER_ID = new QueryColumn(this, "supplier_id");

    public final QueryColumn AUDIT_STATUS = new QueryColumn(this, "audit_status");

    public final QueryColumn TOTAL_AMOUNT = new QueryColumn(this, "total_amount");

    public final QueryColumn AUDIT_SUGGESTION = new QueryColumn(this, "audit_suggestion");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ORDER_NO, CREATE_BY, CREATE_TIME, SUPPLIER_ID, AUDIT_STATUS, TOTAL_AMOUNT, AUDIT_SUGGESTION};

    public PurchaseOrderTableDef() {
        super("", "scm_purchase_order");
    }

}
