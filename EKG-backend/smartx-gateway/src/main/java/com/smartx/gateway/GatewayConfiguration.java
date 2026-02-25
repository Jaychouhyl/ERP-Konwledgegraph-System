package com.smartx.gateway;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

import jakarta.annotation.PostConstruct;

@Configuration
public class GatewayConfiguration {

    @PostConstruct
    public void doInit() {
        // 自定义被限流或熔断时的返回逻辑
        BlockRequestHandler handler = (exchange, t) -> {
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue("{\"code\": 429, \"msg\": \"系统触发熔断或限流，请稍后重试(持久化版)\"}"));
        };
        GatewayCallbackManager.setBlockHandler(handler);
    }
}