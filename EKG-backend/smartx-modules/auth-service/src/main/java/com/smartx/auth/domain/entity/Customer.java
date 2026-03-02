package com.smartx.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("base_customer")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String customerName; // 客户名称
    private String contactPhone; // 联系电话
    private String address;      // 收货地址
    private Date createTime;
    private Integer isDeleted;
}
