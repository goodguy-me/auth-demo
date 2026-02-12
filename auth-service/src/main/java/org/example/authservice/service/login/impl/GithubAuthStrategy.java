package org.example.authservice.service.login.impl;

import lombok.RequiredArgsConstructor;
import org.example.authservice.config.GithubOAuthProperties;
import org.example.authservice.model.dto.LoginRequestDTO;
import org.example.authservice.model.dto.LoginResponseDTO;
import org.example.authservice.service.login.LoginStrategy;
import org.springframework.stereotype.Component;

/**
 * @author ZSZ
 * @date 2026/2/12 8:20
 * @description GITHUB用户登录
 */
@Component("github")
@RequiredArgsConstructor
public class GithubAuthStrategy implements LoginStrategy {
    private final GithubOAuthProperties githubOAuthProperties;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        String authUrl = String.format("%s?client_id=%s&redirect_uri=%s&scope=user:email",
                githubOAuthProperties.getAuthorizeUrl(),
                githubOAuthProperties.getClientId(),
                githubOAuthProperties.getRedirectUri());
        return LoginResponseDTO.builder()
                .redirectUrl(authUrl)
                .build();
    }
}
