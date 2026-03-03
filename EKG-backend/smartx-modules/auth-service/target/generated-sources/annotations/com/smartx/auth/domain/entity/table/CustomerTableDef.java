package com.smartx.auth.domain.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class CustomerTableDef extends TableDef {

    public static final CustomerTableDef CUSTOMER = new CustomerTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn ADDRESS = new QueryColumn(this, "address");

    public final QueryColumn IS_DELETED = new QueryColumn(this, "is_deleted");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn CONTACT_PHONE = new QueryColumn(this, "contact_phone");

    public final QueryColumn CUSTOMER_NAME = new QueryColumn(this, "customer_name");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ADDRESS, IS_DELETED, CREATE_TIME, CONTACT_PHONE, CUSTOMER_NAME};

    public CustomerTableDef() {
        super("", "base_customer");
    }

}
