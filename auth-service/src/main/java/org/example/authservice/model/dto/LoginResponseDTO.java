package org.example.authservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author ZSZ
 * @date 2026/2/12 8:06
 * @description 登录返回对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private Map<String, Object> user;
    private String redirectUrl;   // GitHub 登录第一步使用
    private String errorMessage;
}
