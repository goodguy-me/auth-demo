package org.example.authservice.service.login;

import org.example.authservice.model.dto.LoginRequestDTO;
import org.example.authservice.model.dto.LoginResponseDTO;

/**
 * @author ZSZ
 * @date 2026/2/12 8:14
 * @description 登录策略接口
 */
public interface LoginStrategy {
    LoginResponseDTO login(LoginRequestDTO request);
}
