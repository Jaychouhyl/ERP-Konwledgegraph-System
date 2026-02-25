package com.smartx.finance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/finance") // 注意：这里要对应网关路由配置
public class TestController {

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", "恭喜你！请求已成功穿过网关到达 Auth-Service。");
        map.put("timestamp", System.currentTimeMillis());
        return map;
    }
}