package com.smartx.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartx.auth.domain.entity.User;
import com.smartx.auth.mapper.UserMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.smartx.common.core.domain.Result;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    // 🌟 JWT 签名的密钥（企业级项目一般放配置文件，这里先硬编码）
    private static final String SECRET_KEY = "SmartX_ERP_Secret_Key_DeepSeek_RAG";

    @PostMapping("/login")
    public Result<?> login(@RequestParam("username") String username, 
                           @RequestParam("password") String password) {

        // 1. 去数据库查用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );

        // 2. 校验账号密码
        if (user == null || !user.getPassword().equals(password)) {
            // 🌟 优雅重构：直接抛出失败的 Result，不再手动 put code 和 msg
            return Result.fail(401, "用户名或密码错误！");
        }

        if (user.getStatus() == 0) {
            return Result.fail(401, "该账号已被停用！");
        }

        // 3. 登录成功，生成 JWT Token
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .claim("realName", user.getRealName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        // 4. 将 Token 和用户信息打包 (这里才需要用到 Map)
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", user);

        // 🌟 优雅重构：直接使用 Result.success 返回！
        return Result.success("登录成功", data);
    }
}
