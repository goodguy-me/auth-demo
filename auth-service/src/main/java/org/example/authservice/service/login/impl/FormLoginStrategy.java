package org.example.authservice.service.login.impl;

import lombok.RequiredArgsConstructor;
import org.example.authservice.mapper.UserMapper;
import org.example.authservice.model.dto.LoginRequestDTO;
import org.example.authservice.model.dto.LoginResponseDTO;
import org.example.authservice.model.entity.User;
import org.example.authservice.service.login.LoginStrategy;
import org.example.authservice.utils.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author ZSZ
 * @date 2026/2/12 8:16
 * @description 数据库用户登录
 */
@Component("form")
@RequiredArgsConstructor
public class FormLoginStrategy implements LoginStrategy {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        String username = request.getUsername();
        String password = request.getPassword();

        User user = userMapper.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!user.isEnabled()) {
            throw new RuntimeException("用户已被禁用");
        }

        String token = jwtUtils.generateToken(user);
        return buildSuccessResponse(token, user);
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
