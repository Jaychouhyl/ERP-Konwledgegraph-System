package com.smartx.auth.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table("sys_user")
public class User {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private Long roleId;
    private Integer status;
}
