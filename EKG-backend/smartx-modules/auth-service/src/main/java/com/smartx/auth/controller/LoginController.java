package com.smartx.auth.controller;

import com.mybatisflex.core.query.QueryWrapper;
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

import static com.smartx.auth.domain.entity.table.UserTableDef.USER;

import com.mybatisflex.core.row.Db;
import com.smartx.common.redis.service.RedisService;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    // 🌟 JWT 签名的密钥（企业级项目一般放配置文件，这里先硬编码）
    private static final String SECRET_KEY = "SmartX_ERP_Secret_Key_DeepSeek_RAG";

    @PostMapping("/login")
    public Result<?> login(@RequestParam("username") String username, 
                           @RequestParam("password") String password) {

        // 1. 去数据库查用户
        User user = userMapper.selectOneByQuery(
                QueryWrapper.create()
                        .from(USER)
                        .where(USER.USERNAME.eq(username))
        );

        // 2. 校验账号密码
        if (user == null || !user.getPassword().equals(password)) {
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

        // ================= 🌟 核心：加载用户权限并存入 Redis =================
        List<String> perms;
        if (user.getId() == 1L) {
            // 如果是 admin，默认给他一个 "ALL" 标识
            perms = java.util.Collections.singletonList("*:*:*");
        } else {
            // 1. 普通角色：用 Flex 查出包含数据的 Row 列表
            List<com.mybatisflex.core.row.Row> rows = com.mybatisflex.core.row.Db.selectListBySql(
                    "SELECT DISTINCT m.perms FROM sys_menu m " +
                            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
                            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
                            "WHERE ur.user_id = ? AND m.perms IS NOT NULL AND m.perms != ''",
                    user.getId()
            );

            // 2. 将 Row 对象里的 "perms" 列单独提取出来，转换成纯 String 列表
            perms = rows.stream()
                    .map(row -> row.getString("perms"))
                    .collect(java.util.stream.Collectors.toList());
        }

        // 存入 Redis，有效期 24 小时 (跟 Token 寿命一致)
        redisService.setCacheObject("login:perms:" + user.getId(), perms, 24, TimeUnit.HOURS);
        // ===================================================================

        // 4. 将 Token 和用户信息打包 (这里才需要用到 Map)
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", user);

        // 🌟 优雅重构：直接使用 Result.success 返回！
        return Result.success("登录成功", data);
    }
}
