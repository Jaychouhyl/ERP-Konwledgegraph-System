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
    private String phone;
    private String email;
    private String avatar;
    private String department;
    // private Long roleId; // 已升级为多角色 RBAC，此字段废弃
    private Integer status;
    private String roleName;
}
