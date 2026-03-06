package com.smartx.auth.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.smartx.auth.domain.entity.User;
import com.smartx.auth.mapper.UserMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.smartx.auth.domain.entity.table.UserTableDef.USER;

/**
 * 🔐 登录认证与 Token 核心服务
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    // JWT 秘钥 (真实项目可移至 yml 配置)
    private static final String SECRET_KEY = "SmartX_ERP_Secret_Key_Must_Be_Long_Enough_For_Security";
    // Token 过期时间：24小时
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

    public String login(String username, String password) {
        // 1. 查询数据库里的用户
        User user = userMapper.selectOneByQuery(
                QueryWrapper.create().where(USER.USERNAME.eq(username))
        );

        // 2. 基础安全校验
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("账号或密码错误！");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("该账号已被停用，请联系管理员！");
        }

        // 3. 签发 JWT Token (把 userId 塞进去，给后面的网关和拦截器用！)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}