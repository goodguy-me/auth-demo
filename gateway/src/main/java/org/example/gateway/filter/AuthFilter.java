package org.example.gateway.filter;

/**
 * @author ZSZ
 * @date 2026/2/11 5:51
 * @description
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
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

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private final Set<String> WHITE_LIST = new HashSet<>(Arrays.asList(
            "/auth/login",
            "/auth/github",
            "/auth/github/callback",
            "/auth/ldap"
    ));

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "mySuperSecretKeyForJwtTokenGeneration2024".getBytes(StandardCharsets.UTF_8)
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // 白名单放行
        if (WHITE_LIST.contains(path)) {
            return chain.filter(exchange);
        }

        // 获取Token
        String token = getToken(request);
        if (token == null) {
            return unauthorized(exchange, "Token缺失");
        }

        try {
            // 验证Token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);
            String method = request.getMethod().toString();

            // 检查权限
            if (!checkPermission(path, method, role)) {
                return forbidden(exchange, "权限不足");
            }

            // 添加用户信息到请求头
            ServerHttpRequest newRequest = request.mutate()
                    .header("X-User-Id", claims.get("userId").toString())
                    .header("X-Username", claims.get("username", String.class))
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(newRequest).build());

        } catch (Exception e) {
            return unauthorized(exchange, "Token无效");
        }
    }

    private String getToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean checkPermission(String path, String method, String role) {
        // PRODUCT_ADMIN拥有所有权限
        if ("PRODUCT_ADMIN".equals(role)) {
            return true;
        }

        // EDITOR权限
        if ("EDITOR".equals(role)) {
            if (path.startsWith("/products")) {
                return true; // EDITOR可以访问所有产品接口
            }
            return false;
        }

        // USER权限
        if ("USER".equals(role)) {
            if (path.startsWith("/products") && "GET".equals(method)) {
                return true; // USER只能GET产品
            }
            return false;
        }

        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"code\":403,\"message\":\"%s\"}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
