package org.example.authservice.service;


import org.example.authservice.config.LdapProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * @author ZSZ
 * @date 2026/2/12 1:06
 * @description
 */
@Service
public class LdapService {

    @Autowired
    private LdapProperties ldapProperties;

    /**
     * 认证用户：尝试使用用户名密码进行 LDAP BIND
     */
    public boolean authenticate(String username, String password) {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapProperties.getUrls());
        contextSource.setBase(ldapProperties.getBase());
        contextSource.setUserDn(ldapProperties.getUserDn());
        contextSource.setPassword(ldapProperties.getPassword());
        try {
            contextSource.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("LDAP 上下文初始化失败", e);
        }

        // 先查找用户的完整 DN
        LdapTemplate findTemplate = new LdapTemplate(contextSource);
        String userDn;
        try {
            userDn = findTemplate.searchForObject(
                    LdapQueryBuilder.query()
                            .filter(ldapProperties.getUserSearchFilter(), username),
                    (Attributes attrs) -> {
                        try {
                            return (String) attrs.get("distinguishedName").get();
                        } catch (NamingException e) {
                            // 如果属性名不是 distinguishedName，尝试 entryDN 或构造
                            return LdapUtils.newLdapName(
                                    "uid=" + username + "," + ldapProperties.getBase()
                            ).toString();
                        }
                    }
            );
        } catch (Exception e) {
            return false; // 用户不存在
        }

        // 使用用户 DN 和密码创建新的上下文进行认证
        LdapContextSource authSource = new LdapContextSource();
        authSource.setUrl(ldapProperties.getUrls());
        authSource.setBase(ldapProperties.getBase());
        authSource.setUserDn(userDn);
        authSource.setPassword(password);
        try {
            authSource.afterPropertiesSet();
            authSource.getContext(); // 尝试获取上下文，成功则认证通过
            return true;
        } catch (Exception e) {
            return false; // 密码错误
        }
    }

    /**
     * 获取用户所属的所有组 DN
     */
    public List<String> getUserGroups(String username) {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapProperties.getUrls());
        contextSource.setBase(ldapProperties.getBase());
        contextSource.setUserDn(ldapProperties.getUserDn());
        contextSource.setPassword(ldapProperties.getPassword());
        try {
            contextSource.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("LDAP 上下文初始化失败", e);
        }

        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);

        // 先获取用户的 DN
        String userDn = ldapTemplate.searchForObject(
                LdapQueryBuilder.query()
                        .filter(ldapProperties.getUserSearchFilter(), username),
                (Attributes attrs) -> {
                    try {
                        return attrs.get("distinguishedName").get().toString();
                    } catch (NamingException e) {
                        return "uid=" + username + "," + ldapProperties.getBase();
                    }
                }
        );

        // 查询所有包含该用户的组
        LdapQuery query = LdapQueryBuilder.query()
                .base(ldapProperties.getGroupSearchBase())
                .filter(ldapProperties.getGroupSearchFilter(), userDn);

        return ldapTemplate.search(query, (Attributes attrs) -> {
            try {
                return attrs.get("distinguishedName").get().toString();
            } catch (NamingException e) {
                return null;
            }
        });
    }

    /**
     * 根据组 DN 列表映射系统角色（取最高权限）
     */
    public String mapToRole(List<String> groupDns) {
        Map<String, String> mapping = ldapProperties.getRoleMapping();
        // 按角色等级排序：PRODUCT_ADMIN > EDITOR > USER
        String role = "USER"; // 默认角色
        for (String groupDn : groupDns) {
            String mappedRole = mapping.get(groupDn);
            if (mappedRole != null) {
                if ("PRODUCT_ADMIN".equals(mappedRole)) {
                    return "PRODUCT_ADMIN";
                } else if ("EDITOR".equals(mappedRole) && !"PRODUCT_ADMIN".equals(role)) {
                    role = "EDITOR";
                } else if ("USER".equals(mappedRole) && role == null) {
                    role = "USER";
                }
            }
        }
        return role;
    }
    public String findUserDn(String username) {
        LdapContextSource contextSource = createContextSource();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        return ldapTemplate.searchForObject(
                LdapQueryBuilder.query()
                        .filter(ldapProperties.getUserSearchFilter(), username),
                (Attributes attrs) -> {
                    try {
                        return attrs.get("distinguishedName").get().toString();
                    } catch (NamingException e) {
                        // 尝试构造
                        return "uid=" + username + "," + ldapProperties.getBase();
                    }
                }
        );
    }
}
