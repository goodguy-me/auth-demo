package org.example.authservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZSZ
 * @date 2026/2/12 8:49
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    //用户名
    private String username;
    //密码
    private String password;
    //角色
    private String role;
    //是否禁用
    private boolean enabled;
    //LDAP账户
    private String ldapDn;
}
