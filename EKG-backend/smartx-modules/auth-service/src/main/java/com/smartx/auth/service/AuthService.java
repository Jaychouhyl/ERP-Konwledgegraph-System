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

    // JWT 秘钥
    private static final String SECRET_KEY = "SmartX_ERP_Secret_Key_Must_Be_Long_Enough_For_Security";
    // Token 过期时间：24小时
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

    // 🌟 返回值从 String 改为了 Map，同时包容 Token 和 UserInfo
    public Map<String, Object> login(String username, String password) {
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

        // 3. 签发 JWT Token
        // 在 AuthService.login() 构建 userInfo 的地方，把以下字段全部带上：
        Map<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("id", user.getId());
        userInfoMap.put("username", user.getUsername());
        userInfoMap.put("realName", user.getRealName());
        userInfoMap.put("phone", user.getPhone());          // 🆕
        userInfoMap.put("email", user.getEmail());            // 🆕
        userInfoMap.put("department", user.getDepartment());  // 🆕
        userInfoMap.put("avatar", user.getAvatar());          // 🆕
        userInfoMap.put("roleName", user.getRoleName());      // 🆕
        userInfoMap.put("status", user.getStatus());

        String token = Jwts.builder()
                .setClaims(userInfoMap)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        // 4. 🌟 核心安全补丁：把密码清空，绝对不能传给前端！
        user.setPassword(null);

        // 5. 拼装前端需要的完整结构
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);

        return result;
    }
}