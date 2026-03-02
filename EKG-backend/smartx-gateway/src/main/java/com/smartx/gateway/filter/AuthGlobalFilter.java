package com.smartx.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    // 必须和 auth-service 里的密钥一模一样！
    private static final String SECRET_KEY = "SmartX_ERP_Secret_Key_DeepSeek_RAG";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 🌟 1. 白名单放行：登录接口不需要校验 Token
        if (path.contains("/auth/login")) {
            return chain.filter(exchange);
        }

        // 🌟 2. 拦截并获取 Token (通常前端会放在 Header 的 Authorization 字段里)
        String token = request.getHeaders().getFirst("Authorization");

        if (token == null || token.isEmpty()) {
            return unauthorizedResponse(exchange.getResponse(), "您还未登录，请先登录！(Missing Token)");
        }

        // 🌟 3. 解析和校验 Token
        try {
            // 如果前端传的是 "Bearer eyJhbGci..." 这种标准格式，需要截取掉前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            // 🌟 4. 最强一步：上下文透传！
            // 把 Token 里解析出来的 userId 塞进请求头，让后面的微服务都知道是谁在操作！
            String userId = claims.get("userId").toString();
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .build();

            // 放行，并把修改过 Header 的请求传给下游微服务
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            // Token 过期或被篡改，抛出异常
            return unauthorizedResponse(exchange.getResponse(), "登录已过期或身份不合法，请重新登录！(Invalid Token)");
        }
    }

    // 设置在所有过滤器之前执行
    @Override
    public int getOrder() {
        return -100;
    }

    // 构造 401 失败响应
    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String msg) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String json = "{\"code\": 401, \"msg\": \"" + msg + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
