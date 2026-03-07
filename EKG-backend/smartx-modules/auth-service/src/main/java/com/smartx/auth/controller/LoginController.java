package com.smartx.auth.controller;

import com.smartx.auth.service.AuthService;
import com.smartx.common.core.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
            // 一行代码调起核心业务，拿到完整的 token 和 userInfo
            Map<String, Object> data = authService.login(username, password);

            return Result.success("登录成功！", data);

        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}