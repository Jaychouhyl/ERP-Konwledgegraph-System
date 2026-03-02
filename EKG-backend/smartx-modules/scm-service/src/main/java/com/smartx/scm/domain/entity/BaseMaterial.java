package com.smartx.scm.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("base_material")
public class BaseMaterial {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String materialCode; // 物料编码
    private String materialName; // 物料名称
    private String materialType; // 类型 (RAW:原材料, FINISHED:成品)
    private String unit;         // 计量单位
    private BigDecimal standardCost; // 标准成本
    private String description;  // 描述
    private Date createTime;
}
