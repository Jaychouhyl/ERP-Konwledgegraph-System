package com.smartx.sales;

import com.smartx.common.core.config.FeignInterceptorConfig; // 🌟 引入拦截器
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
// 🌟 1. 扩大扫描范围，确保能扫描到 common 包里的组件
@SpringBootApplication(scanBasePackages = {"com.smartx"})
// 🌟 2. 强制所有的 Feign 客户端在出门时都必须带上我们写的拦截器！
@EnableFeignClients(basePackages = {"com.smartx.api"}, defaultConfiguration = FeignInterceptorConfig.class)
public class SalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesApplication.class, args);
        System.out.println("====== Sales Service 启动成功 ======");
    }
}