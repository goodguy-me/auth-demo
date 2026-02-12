package org.example.authservice.service.login.impl;

import lombok.RequiredArgsConstructor;
import org.example.authservice.config.LdapProperties;
import org.example.authservice.mapper.UserMapper;
import org.example.authservice.model.dto.LoginRequestDTO;
import org.example.authservice.model.dto.LoginResponseDTO;
import org.example.authservice.model.entity.User;
import org.example.authservice.service.LdapService;
import org.example.authservice.service.login.LoginStrategy;
import org.example.authservice.utils.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author ZSZ
 * @date 2026/2/12 8:20
 * @description LDAP用户登录
 */
@Component("ldap")
@RequiredArgsConstructor
public class LdapLoginStrategy implements LoginStrategy {
    private final LdapService ldapService;
    private final LdapProperties ldapProperties;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        String username = request.getUsername();
        String password = request.getPassword();

        // 1. LDAP 认证
        if (!ldapService.authenticate(username, password)) {
            throw new RuntimeException("LDAP 认证失败，用户名或密码错误");
        }

        // 2. 获取 DN 和组信息
        String userDn = ldapService.findUserDn(username);
        List<String> groups = ldapService.getUserGroups(username);
        String role = ldapService.mapToRole(groups);

        // 3. 查找或创建用户（逻辑同原代码）
        User user = userMapper.findByLdapDn(userDn);
        if (user == null) {
            user = userMapper.findByUsername(username);
            if (user != null) {
                user.setLdapDn(userDn);
                user.setRole(role);
                userMapper.updateUser(user);
            } else {
                user = new User();
                user.setUsername(username);
                user.setPassword(null);
                user.setRole(role);
                user.setEnabled(true);
                user.setLdapDn(userDn);
                userMapper.insertUser(user);
            }
        } else {
            if (!role.equals(user.getRole())) {
                user.setRole(role);
                userMapper.updateUser(user);
            }
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
