package org.example.authservice.service.oauth2;

/**
 * @author ZSZ
 * @date 2026/2/13 6:50
 * @description
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.authservice.model.entity.User;
import org.example.authservice.utils.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 1. 获取 OAuth2User
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 2. 从 attributes 中提取 user_id 和 role
        Long userId = oAuth2User.getAttribute("user_id");
        String role = oAuth2User.getAttribute("role");
        String username = oAuth2User.getAttribute("login"); // 或 oAuth2User.getName()

        // 3. 生成 JWT Token（使用你已有的 JwtUtils）
        String token = jwtUtils.generateToken(User.builder().id(userId).role(role).username(username).build());

        // 4. 构造响应体（与现有 LoginResponseDTO 格式一致）
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("accessToken", token);
        responseBody.put("tokenType", "Bearer");
        responseBody.put("expiresIn", 7200L);
        responseBody.put("user", Map.of(
                "id", userId,
                "username", username,
                "role", role
        ));
        responseBody.put("info", "登录成功");

        // 5. 输出 JSON
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
