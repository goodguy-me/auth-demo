package org.example.authservice.service.login.impl;

import lombok.RequiredArgsConstructor;
import org.example.authservice.config.GithubClient;
import org.example.authservice.config.GithubOAuthProperties;
import org.example.authservice.mapper.UserMapper;
import org.example.authservice.model.dto.LoginResponseDTO;
import org.example.authservice.model.entity.User;
import org.example.authservice.utils.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @author ZSZ
 * @date 2026/2/12 8:29
 * @description GITHUB oauth2登录回调
 */
@Component
@RequiredArgsConstructor
public class GithubCallbackStrategy {
    private final GithubClient githubClient;
    private final GithubOAuthProperties githubOAuthProperties;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public LoginResponseDTO handleCallback(String code) {
        try {
            // 1. 获取 access token
            GithubClient.AccessTokenResponse tokenResponse = githubClient.getAccessToken(
                    code,
                    githubOAuthProperties.getClientId(),
                    githubOAuthProperties.getClientSecret(),
                    githubOAuthProperties.getRedirectUri()
            );

            // 2. 获取用户信息
            GithubClient.GithubUser githubUser = githubClient.getUserInfo(tokenResponse.getAccessToken());

            // 3. 查找或创建用户（逻辑同原代码，简化）
            User user = userMapper.findByUsername(githubUser.getLogin() + "_github");
            if (user == null) {
                user = new User();
                user.setUsername(githubUser.getLogin() + "_github");
                user.setRole("EDITOR");
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                userMapper.insertUser(user);
            } else {
                user.setUsername(githubUser.getLogin() + "_github");
                if (!"EDITOR".equals(user.getRole())) {
                    user.setRole("EDITOR");
                }
                userMapper.updateUser(user);
            }

            String token = jwtUtils.generateToken(user);
            return buildSuccessResponse(token, user);

        } catch (Exception e) {
            return LoginResponseDTO.builder()
                    .errorMessage("GitHub 登录失败: " + e.getMessage())
                    .build();
        }
    }

    private LoginResponseDTO buildSuccessResponse(String token, User user) {
        return LoginResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(7200L)
                .user(Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole()
                ))
                .build();
    }
}
