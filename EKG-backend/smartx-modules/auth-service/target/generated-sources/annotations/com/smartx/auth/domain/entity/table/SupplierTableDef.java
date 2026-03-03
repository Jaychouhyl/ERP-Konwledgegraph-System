package com.smartx.auth.domain.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SupplierTableDef extends TableDef {

    public static final SupplierTableDef SUPPLIER = new SupplierTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn IS_DELETED = new QueryColumn(this, "is_deleted");

    public final QueryColumn RISK_LEVEL = new QueryColumn(this, "risk_level");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn CONTACT_PHONE = new QueryColumn(this, "contact_phone");

    public final QueryColumn SUPPLIER_NAME = new QueryColumn(this, "supplier_name");

    public final QueryColumn CONTACT_PERSON = new QueryColumn(this, "contact_person");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, IS_DELETED, RISK_LEVEL, CREATE_TIME, CONTACT_PHONE, SUPPLIER_NAME, CONTACT_PERSON};

    public SupplierTableDef() {
        super("", "base_supplier");
    }

}
