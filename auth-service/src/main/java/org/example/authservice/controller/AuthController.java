package org.example.authservice.controller;

/**
 * @author ZSZ
 * @date 2026/2/11 6:42
 * @description
 */

import org.example.authservice.config.GithubClient;
import org.example.authservice.config.GithubOAuthProperties;
import org.example.authservice.mapper.UserMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GithubOAuthProperties githubOAuthProperties;

    @Autowired
    private GithubClient githubClient;

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "mySuperSecretKeyForJwtTokenGeneration2024".getBytes(StandardCharsets.UTF_8)
    );
    @ResponseBody
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        UserMapper.User user = userMapper.findByUsername(username);
        String encode = passwordEncoder.encode(password);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Map.of(
                    "errormessage","用户名或密码错误"
            );
        }
        if(!user.isEnabled()){
            return Map.of(
                    "errormessage","用户已被禁用"
            );
        }


        // 生成Token
        String token = Jwts.builder()
                .setClaims(new HashMap<String, Object>() {{
                    put("userId", user.getId());
                    put("username", user.getUsername());
                    put("role", user.getRole());
                }})
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7200000)) // 2小时
                .signWith(SECRET_KEY)
                .compact();

        return Map.of(
                "accessToken", token,
                "tokenType", "Bearer",
                "expiresIn", 7200,
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole()
                )
        );
    }
    /**
     * 重定向到 GitHub 授权页面
     */
    @GetMapping("/github")
    public RedirectView githubAuth() {
        String authUrl = String.format("%s?client_id=%s&redirect_uri=%s&scope=user:email",
                githubOAuthProperties.getAuthorizeUrl(),
                githubOAuthProperties.getClientId(),
                githubOAuthProperties.getRedirectUri());
        RedirectView redirectView = new RedirectView(authUrl);
        return redirectView;
    }

    /**
     * GitHub OAuth2 回调接口
     */
    @ResponseBody
    @GetMapping("/github/callback")
    public Map<String, Object> githubCallback(@RequestParam("code") String code) {
        try {
            // 1. 获取 access token
            GithubClient.AccessTokenResponse tokenResponse = githubClient.getAccessToken(
                    code,
                    githubOAuthProperties.getClientId(),
                    githubOAuthProperties.getClientSecret(),
                    githubOAuthProperties.getRedirectUri()
            );

            if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
                throw new RuntimeException("获取 GitHub Access Token 失败");
            }

            // 2. 获取用户信息
            GithubClient.GithubUser githubUser = githubClient.getUserInfo(
                    tokenResponse.getAccessToken()
            );

            // 3. 获取用户邮箱（GitHub 用户邮箱可能需要单独请求）
//            String email = githubUser.getEmail();
//            if (email == null || email.isEmpty()) {
//                List<GithubClient.GithubEmail> emails = githubClient.getUserEmails(
//                        tokenResponse.getAccessToken()
//                );
//                if (emails != null && !emails.isEmpty()) {
//                    email = emails.stream()
//                            .filter(GithubClient.GithubEmail::getPrimary)
//                            .filter(GithubClient.GithubEmail::getVerified)
//                            .map(GithubClient.GithubEmail::getEmail)
//                            .findFirst()
//                            .orElse(emails.get(0).getEmail());
//                }
//            }

            // 4. 查找或创建用户
//            UserMapper.User user = userMapper.findByGithubId(githubUser.getId());
            UserMapper.User user = userMapper.findByUsername(githubUser.getLogin() + "_github");

//            if (user == null && email != null) {
//                // 尝试通过邮箱查找用户（可能已经用邮箱注册过）
//                user = userMapper.findByEmail(email);
//            }

            if (user == null) {
                // 创建新用户
                user = new UserMapper.User();
                user.setUsername(githubUser.getLogin() + "_github");
//                user.setEmail(email != null ? email : githubUser.getLogin() + "@github.com");
//                user.setGithubId(githubUser.getId());
//                user.setAvatarUrl(githubUser.getAvatarUrl());
                user.setRole("EDITOR"); // 赋予 EDITOR 角色

                // 生成随机密码（GitHub 登录不需要密码）
                String randomPassword = UUID.randomUUID().toString();
                user.setPassword(passwordEncoder.encode(randomPassword));

                userMapper.insertUser(user);
            } else {
                // 更新现有用户信息
                user.setUsername(githubUser.getLogin() + "_github");
//                user.setEmail(email != null ? email : githubUser.getLogin() + "@github.com");
//                user.setGithubId(githubUser.getId());
//                user.setAvatarUrl(githubUser.getAvatarUrl());

                // 确保用户角色为 EDITOR（如果之前不是）
                if (!"EDITOR".equals(user.getRole())) {
                    user.setRole("EDITOR");
                }

                userMapper.updateUser(user);
            }

            // 5. 生成并返回 JWT token
            return generateAuthResponse(user);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("GitHub 登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/ldap")
    @ResponseBody
    public Map<String, Object> ldapLogin(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        // 1. LDAP 认证
        boolean authenticated = ldapService.authenticate(username, password);
        if (!authenticated) {
            throw new RuntimeException("LDAP 认证失败，用户名或密码错误");
        }

        // 2. 获取 LDAP 用户 DN 和组信息
        String userDn = "uid=" + username + "," + ldapProperties.getBase(); // 简化，实际需从LDAP查询
        List<String> groups = ldapService.getUserGroups(username);
        String role = ldapService.mapToRole(groups);

        // 3. 查找或创建本地用户
        UserMapper.User user = userMapper.findByLdapDn(userDn);
        if (user == null) {
            // 尝试用 username 查找，可能已存在（非LDAP用户）
            user = userMapper.findByUsername(username);
            if (user != null) {
                // 更新该用户，关联 LDAP DN，并更新角色（按LDAP映射）
                user.setLdapDn(userDn);
                user.setRole(role);
                userMapper.updateUser(user);
            } else {
                // 创建新 LDAP 用户
                user = new UserMapper.User();
                user.setUsername(username);
                user.setPassword(null); // LDAP 用户不需要本地密码
                user.setRole(role);
                user.setEnabled(true);
                user.setLdapDn(userDn);
                userMapper.insertUser(user);
            }
        } else {
            // 更新角色（如果需要）
            if (!role.equals(user.getRole())) {
                user.setRole(role);
                userMapper.updateUser(user);
            }
        }

        // 4. 生成 JWT Token 并返回（复用 generateAuthResponse）
        return generateAuthResponse(user);
    }

    /**
     * 生成认证响应
     */
    private Map<String, Object> generateAuthResponse(UserMapper.User user) {
        // 生成 Token
        String token = Jwts.builder()
                .setClaims(new HashMap<String, Object>() {{
                    put("userId", user.getId());
                    put("username", user.getUsername());
                    put("role", user.getRole());
//                    if (user.getGithubId() != null) {
//                        put("githubId", user.getGithubId());
//                    }
                }})
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7200000)) // 2小时
                .signWith(SECRET_KEY)
                .compact();

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("tokenType", "Bearer");
        response.put("expiresIn", 7200);
        response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
//                "email", user.getEmail(),
                "role", user.getRole()
//                "avatarUrl", user.getAvatarUrl(),
//                "githubId", user.getGithubId()
        ));

        return response;
    }
}
