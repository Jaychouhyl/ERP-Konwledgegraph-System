package com.smartx.scm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.smartx.scm.api")
@MapperScan("com.smartx.scm.mapper")
public class ScmApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScmApplication.class, args);
    }
}
