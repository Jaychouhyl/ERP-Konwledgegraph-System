package com.smartx.scm.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("scm_inventory")
public class ScmInventory {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long materialId;      // 关联物料ID
    private Integer currentQuantity; // 当前库存数量
    private Integer safeQuantity;    // 安全库存阈值
    private String warehouseLocation;// 库位编码
    private Date updateTime;
}
