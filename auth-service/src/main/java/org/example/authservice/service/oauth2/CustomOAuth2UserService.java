package org.example.authservice.service.oauth2;

/**
 * @author ZSZ
 * @date 2026/2/13 6:49
 * @description
 */

import lombok.RequiredArgsConstructor;
import org.example.authservice.mapper.UserMapper;
import org.example.authservice.model.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 调用默认的 OAuth2UserService 获取用户信息（会调用 user-info-uri）
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // 2. 获取 GitHub 返回的属性
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        String login = (String) attributes.get("login");
        String name = (String) attributes.get("name");


        // 3. 根据 githubId 查找本地用户，若不存在则创建
        User user = userMapper.findByUsername(name);
        if (user == null) {
            // 也可尝试根据用户名查找（防止重复创建）
            user = userMapper.findByUsername(login + "_github");
        }

        if (user == null) {
            user = new User();
            user.setUsername(login + "_github");
            user.setRole("EDITOR");   // 统一赋予 EDITOR 角色
            user.setEnabled(true);
            // 随机密码，不可用于本地登录
            user.setPassword(UUID.randomUUID().toString());
            userMapper.insertUser(user);
        } else {
            // 更新用户信息（每次登录同步最新数据）
            user.setUsername(login + "_github");
            if (!"EDITOR".equals(user.getRole())) {
                user.setRole("EDITOR");
            }
            userMapper.updateUser(user);
        }

        // 4. 将用户ID和角色放入 attributes，便于成功处理器使用
        attributes.put("user_id", user.getId());
        attributes.put("role", user.getRole());

        // 5. 返回 OAuth2User，并授予角色权限（Spring Security 要求前缀 ROLE_）
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                attributes,
                "id"   // 作为 name 的属性名
        );
    }
}
