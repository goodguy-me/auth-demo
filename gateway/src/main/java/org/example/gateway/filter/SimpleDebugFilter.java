package org.example.gateway.filter;

/**
 * @author ZSZ
 * @date 2026/2/11 8:51
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
public class SimpleDebugFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("🎯 [Filter] 请求进入 - 方法: {}, 路径: {}", exchange.getRequest().getMethod(), exchange.getRequest().getPath());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("✅ [Filter] 请求完成 - 路径: {}, 状态码: {}", exchange.getRequest().getPath(), exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
