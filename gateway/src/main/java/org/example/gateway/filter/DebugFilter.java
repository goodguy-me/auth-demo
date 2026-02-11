package org.example.gateway.filter;

/**
 * @author ZSZ
 * @date 2026/2/11 8:08
 * @description
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DebugFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        // 记录所有请求
        log.info("=== GLOBAL FILTER 被调用 ===");
        log.info("请求路径: {}", path);
        log.info("请求方法: {}", method);
        log.info("请求URL: {}", exchange.getRequest().getURI());
        log.info("Query参数: {}", exchange.getRequest().getQueryParams());
        log.info("Headers: {}", exchange.getRequest().getHeaders().toSingleValueMap());

        // 继续过滤器链
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
