package com.smartx.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("base_supplier")
public class Supplier {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String supplierName; // 供应商名称
    private String contactPerson;// 联系人
    private String contactPhone; // 联系电话
    private String riskLevel;    // 风险等级 (HIGH/MEDIUM/LOW)
    private Date createTime;
    private Integer isDeleted;
}
