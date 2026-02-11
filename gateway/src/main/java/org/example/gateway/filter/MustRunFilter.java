package org.example.gateway.filter;

/**
 * @author ZSZ
 * @date 2026/2/11 8:54
 * @description
 */

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MustRunFilter implements GlobalFilter {

    @PostConstruct
    public void init() {
        log.info("🚨 MUST-RUN FILTER 已初始化！");
        log.info("🚨 这个Filter必须被调用！");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        log.info("🔥🔥🔥 FILTER 被调用！ 🔥🔥🔥");
        log.info("🔥 方法: {}", method);
        log.info("🔥 路径: {}", path);
        log.info("🔥 时间: {}", System.currentTimeMillis());
        log.info("🔥 请求ID: {}", exchange.getRequest().getId());

        // 在所有响应头中添加标记
        exchange.getResponse().getHeaders().add("X-Gateway-Filter", "EXECUTED");
        exchange.getResponse().getHeaders().add("X-Filter-Timestamp", String.valueOf(System.currentTimeMillis()));

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("✅ FILTER 完成 - 路径: {}", path);
        }));
    }
}
