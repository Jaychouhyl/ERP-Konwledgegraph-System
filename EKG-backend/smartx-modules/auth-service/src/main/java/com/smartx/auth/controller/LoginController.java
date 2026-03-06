package com.smartx.auth.controller;

import com.smartx.auth.service.AuthService;
import com.smartx.common.core.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 🚪 统一登录入口
 */
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam("username") String username,
                                             @RequestParam("password") String password) {
        try {
            // 一行代码调起核心业务
            String token = authService.login(username, password);

            // 封装给前端的极佳格式
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            return Result.success("登录成功！起飞！", data);

        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}