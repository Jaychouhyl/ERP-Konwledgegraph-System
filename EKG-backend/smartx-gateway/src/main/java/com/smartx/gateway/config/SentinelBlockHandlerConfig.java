package com.smartx.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Sentinel 网关限流/熔断自定义异常返回配置
 */
@Configuration
public class SentinelBlockHandlerConfig {

    @PostConstruct
    public void initBlockHandlers() {
        BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
                // 封装与前端约定的标准 JSON 格式
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("code", 429);
                responseMap.put("msg", "系统压力过大，请求已被 Sentinel 网关自动拦截，请稍后再试！");
                responseMap.put("data", null);

                // 强制返回 HTTP 状态码 429 (Too Many Requests)
                return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(responseMap));
            }
        };

        // 将自定义的处理器注册到 Sentinel 网关回调管理器中
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
}