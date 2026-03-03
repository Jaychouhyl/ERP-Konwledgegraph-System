package com.smartx.scm.domain.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class ScmInventoryTableDef extends TableDef {

    public static final ScmInventoryTableDef SCM_INVENTORY = new ScmInventoryTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn MATERIAL_ID = new QueryColumn(this, "material_id");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn SAFE_QUANTITY = new QueryColumn(this, "safe_quantity");

    public final QueryColumn CURRENT_QUANTITY = new QueryColumn(this, "current_quantity");

    public final QueryColumn WAREHOUSE_LOCATION = new QueryColumn(this, "warehouse_location");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, MATERIAL_ID, UPDATE_TIME, SAFE_QUANTITY, CURRENT_QUANTITY, WAREHOUSE_LOCATION};

    public ScmInventoryTableDef() {
        super("", "scm_inventory");
    }

}
