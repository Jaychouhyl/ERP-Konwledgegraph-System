package com.smartx.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/user") // 注意不要加 /api
public class UserController {

    @GetMapping("/getName")
    public String getUserNameById(@RequestParam("userId") Long userId) {
        System.out.println("====== 收到查询用户请求，ID：" + userId + " ======");
        return "管理员_" + userId; // 模拟返回用户名
    }
}
