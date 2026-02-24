package com.smartx.scm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.smartx.scm.infrastructure.mapper")
public class ScmApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScmApplication.class, args);
    }
}
