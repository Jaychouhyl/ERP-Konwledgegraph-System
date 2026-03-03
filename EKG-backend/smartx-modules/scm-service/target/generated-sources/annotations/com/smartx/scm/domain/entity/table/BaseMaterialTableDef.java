package com.smartx.scm.domain.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class BaseMaterialTableDef extends TableDef {

    public static final BaseMaterialTableDef BASE_MATERIAL = new BaseMaterialTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn UNIT = new QueryColumn(this, "unit");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    public final QueryColumn MATERIAL_CODE = new QueryColumn(this, "material_code");

    public final QueryColumn MATERIAL_NAME = new QueryColumn(this, "material_name");

    public final QueryColumn MATERIAL_TYPE = new QueryColumn(this, "material_type");

    public final QueryColumn STANDARD_COST = new QueryColumn(this, "standard_cost");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, UNIT, CREATE_TIME, DESCRIPTION, MATERIAL_CODE, MATERIAL_NAME, MATERIAL_TYPE, STANDARD_COST};

    public BaseMaterialTableDef() {
        super("", "base_material");
    }

}
