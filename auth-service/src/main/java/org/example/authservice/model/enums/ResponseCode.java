package org.example.authservice.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseCode {

    LOGIN_SUCCESS("0000", "登录成功"),
    LOGIN_FAILED("0001", "登录失败"),
    LGIN_ERROR("0002", "不支持的登录类型"),
    ILLEGAL_PARAMETER("0002", "非法参数")
    ;

    private String code;
    private String info;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public enum Login {
        TOKEN_ERROR("0003", "登录权限拦截"),

        ;

        private String code;
        private String info;
    }

}
