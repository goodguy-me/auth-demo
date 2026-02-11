package org.example.authservice.config;

/**
 * @author ZSZ
 * @date 2026/2/12 1:04
 * @description LDAP登录服务配置类
 */
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ldap")
public class LdapProperties {
    private String urls;
    private String base;
    private String userDn;
    private String password;
    private String userSearchFilter;
    private String groupSearchBase;
    private String groupSearchFilter;
    private Map<String, String> roleMapping = new HashMap<>();

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getUserDn() {
        return userDn;
    }

    public void setUserDn(String userDn) {
        this.userDn = userDn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserSearchFilter() {
        return userSearchFilter;
    }

    public void setUserSearchFilter(String userSearchFilter) {
        this.userSearchFilter = userSearchFilter;
    }

    public String getGroupSearchBase() {
        return groupSearchBase;
    }

    public void setGroupSearchBase(String groupSearchBase) {
        this.groupSearchBase = groupSearchBase;
    }

    public String getGroupSearchFilter() {
        return groupSearchFilter;
    }

    public void setGroupSearchFilter(String groupSearchFilter) {
        this.groupSearchFilter = groupSearchFilter;
    }

    public Map<String, String> getRoleMapping() {
        return roleMapping;
    }

    public void setRoleMapping(Map<String, String> roleMapping) {
        this.roleMapping = roleMapping;
    }
}
