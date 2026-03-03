package com.smartx.auth.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import java.util.Date;

@Data
@Table("base_customer")
public class Customer {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private String customerName; // 客户名称
    private String contactPhone; // 联系电话
    private String address;      // 收货地址
    private Date createTime;
    private Integer isDeleted;
}
