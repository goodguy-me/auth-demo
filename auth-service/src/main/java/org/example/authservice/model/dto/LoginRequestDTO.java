package org.example.authservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZSZ
 * @date 2026/2/12 8:06
 * @description 登录请求对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    // form, ldap, github
    private String type;
    private String username;
    private String password;
}
