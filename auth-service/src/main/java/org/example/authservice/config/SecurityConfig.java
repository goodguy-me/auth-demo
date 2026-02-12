package org.example.authservice.config;

/**
 * @author ZSZ
 * @date 2026/2/11 6:41
 * @description
 */

import lombok.RequiredArgsConstructor;
import org.example.authservice.service.oauth2.CustomOAuth2UserService;
import org.example.authservice.service.oauth2.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)  // 禁用CSRF
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/auth/login").permitAll()
//                        .requestMatchers("/auth/github/callback").permitAll()
//                        .anyRequest().authenticated()
//                );
//
//        return http.build();
//    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 🔥 1. 使用 lambda 方式禁用 CSRF（Spring Security 6.1+ 推荐）
                .csrf(csrf -> csrf.disable())

                // 2. 无状态会话（JWT）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. 请求授权（使用新的 authorizeHttpRequests + requestMatchers）
                .authorizeHttpRequests(auth -> auth
                        // 公开端点（无需认证）
                        .requestMatchers(
                                "/auth/login",
                                "/auth/ldap",
                                "/auth/github/callback",
                                "/login/**",
                                "/oauth2/**"
                        ).permitAll()
                        // 所有其他请求需要认证
                        .anyRequest().authenticated()
                )

                // 4. OAuth2 登录配置
                .oauth2Login(oauth2 -> oauth2
                        // 自定义登录页面入口（可选）
                        .loginPage("/auth/github")
                        // 授权端点配置
                        .authorizationEndpoint(authEndpoint -> authEndpoint
                                .baseUri("/oauth2/authorization")
                        )
                        // 重定向端点配置（回调地址）
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*")
                        )
                        // 用户信息服务（自定义）
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // 认证成功处理器（返回 JWT）
                        .successHandler(oAuth2LoginSuccessHandler)
                );
        return http.build();
    }

}
