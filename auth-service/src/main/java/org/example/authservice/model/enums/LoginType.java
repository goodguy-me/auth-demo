package org.example.authservice.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 规则过滤校验类型值对象
 * @create 2024-01-06 11:10
 */
@Getter
@AllArgsConstructor
public enum LoginType {
    LDAP("ldap","数据库登录"),
    FORM("form","表单登录"),
    GITHUB("github","GITHUB Oauth2登录"),
    ;

    private final String type;
    private final String info;


}
