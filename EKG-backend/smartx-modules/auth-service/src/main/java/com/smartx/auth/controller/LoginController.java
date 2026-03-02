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

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    // 🌟 JWT 签名的密钥（企业级项目一般放配置文件，这里先硬编码）
    private static final String SECRET_KEY = "SmartX_ERP_Secret_Key_DeepSeek_RAG";

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam("username") String username, 
                                     @RequestParam("password") String password) {
        Map<String, Object> result = new HashMap<>();

        // 1. 去数据库查用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );

        // 2. 校验账号密码 (这里为了演示直接比对明文，实际应比对 MD5 等加密串)
        if (user == null || !user.getPassword().equals(password)) {
            result.put("code", 401);
            result.put("msg", "用户名或密码错误！");
            return result;
        }

        if (user.getStatus() == 0) {
            result.put("code", 401);
            result.put("msg", "该账号已被停用！");
            return result;
        }

        // 3. 🌟 登录成功，生成 JWT Token
        String token = Jwts.builder()
                .setSubject(user.getUsername()) // 主题：用户名
                .claim("userId", user.getId())  // 🌟 核心：把用户ID塞进Token，供后续服务追踪
                .claim("realName", user.getRealName())
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 过期时间：24小时
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 签名算法
                .compact();

        // 4. 返回前端
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", user);

        result.put("code", 0);
        result.put("msg", "登录成功");
        result.put("data", data);
        return result;
    }
}
