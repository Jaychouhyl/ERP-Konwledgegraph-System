package com.smartx.auth.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import java.util.Date;

@Data
@Table("base_supplier")
public class Supplier {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private String supplierName; // 供应商名称
    private String contactPerson;// 联系人
    private String contactPhone; // 联系电话
    private String riskLevel;    // 风险等级 (HIGH/MEDIUM/LOW)
    private Date createTime;
    private Integer isDeleted;
}
