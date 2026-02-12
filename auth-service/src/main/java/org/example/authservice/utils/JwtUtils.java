package org.example.authservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import org.example.authservice.model.entity.User;
/**
 * @author ZSZ
 * @date 2026/2/12 8:22
 * @description 根据用户信息生成Token
 */
@Component
public class JwtUtils {
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "mySuperSecretKeyForJwtTokenGeneration2024".getBytes(StandardCharsets.UTF_8)
    );

    public String generateToken(User user) {
        return Jwts.builder()
                .setClaims(Map.of(
                        "userId", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole()
                ))
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7200000))
                .signWith(SECRET_KEY)
                .compact();
    }
}
