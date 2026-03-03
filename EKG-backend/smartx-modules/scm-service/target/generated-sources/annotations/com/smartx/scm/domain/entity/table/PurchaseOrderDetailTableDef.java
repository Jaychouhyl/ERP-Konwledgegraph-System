package com.smartx.scm.domain.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class PurchaseOrderDetailTableDef extends TableDef {

    public static final PurchaseOrderDetailTableDef PURCHASE_ORDER_DETAIL = new PurchaseOrderDetailTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn QUANTITY = new QueryColumn(this, "quantity");

    public final QueryColumn UNIT_PRICE = new QueryColumn(this, "unit_price");

    public final QueryColumn MATERIAL_ID = new QueryColumn(this, "material_id");

    public final QueryColumn TOTAL_PRICE = new QueryColumn(this, "total_price");

    public final QueryColumn PURCHASE_ORDER_ID = new QueryColumn(this, "purchase_order_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, QUANTITY, UNIT_PRICE, MATERIAL_ID, TOTAL_PRICE, PURCHASE_ORDER_ID};

    public PurchaseOrderDetailTableDef() {
        super("", "scm_purchase_detail");
    }

}
