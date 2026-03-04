package com.smartx.common.security.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermissions {
    // 这里用来存放权限标识，比如 "scm:purchase:add"
    String value();
}
