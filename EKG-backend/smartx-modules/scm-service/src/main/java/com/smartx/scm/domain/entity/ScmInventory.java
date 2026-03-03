package com.smartx.scm.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import java.util.Date;

@Data
@Table("scm_inventory")
public class ScmInventory {
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private Long materialId;      // 关联物料ID
    private Integer currentQuantity; // 当前库存数量
    private Integer safeQuantity;    // 安全库存阈值
    private String warehouseLocation;// 库位编码
    private Date updateTime;
}
