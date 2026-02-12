package org.example.authservice.service;


import org.example.authservice.config.LdapProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;


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
        LdapContextSource contextSource = createContextSource();
        // 先查找用户的完整 DN
        LdapTemplate findTemplate = new LdapTemplate(contextSource);
        String userDn;
        try {
            userDn = findTemplate.searchForObject(
                    LdapQueryBuilder.query()
                            .filter(ldapProperties.getUserSearchFilter(), username),
                    new ContextMapper<String>() {
                        @Override
                        public String mapFromContext(Object ctx) {
                            return ((DirContextOperations) ctx).getNameInNamespace();
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
            authSource.getContext(userDn, password); // 尝试获取上下文，成功则认证通过
            return true;
        } catch (Exception e) {
            return false; // 密码错误
        }
    }
    /**
     * 认证用户：尝试使用用户名密码进行 LDAP BIND
     */
//    public boolean authenticate(String username, String password) {
//        LdapContextSource contextSource = createContextSource();
//        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
//
//        AndFilter filter = new AndFilter();
//        filter.and(new EqualsFilter(ldapProperties.getUserSearchFilter(), username));
//
//        return ldapTemplate.authenticate(
//                LdapUtils.emptyLdapName(),
//                filter.toString(),
//                password
//        );
//    }
    /**
     * 获取用户所属的所有组 DN
     */
    public List<String> getUserGroups(String username) {
        LdapContextSource contextSource = createContextSource();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);

        // 先获取用户的 DN
        String userDn = ldapTemplate.searchForObject(
                LdapQueryBuilder.query()
                        .filter(ldapProperties.getUserSearchFilter(), username),
                new ContextMapper<String>() {
                    @Override
                    public String mapFromContext(Object ctx) {
                        return ((DirContextOperations) ctx).getNameInNamespace();
                    }
                }
        );

        // 查询所有包含该用户的组
        LdapQuery query = LdapQueryBuilder.query()
                .base(ldapProperties.getGroupSearchBase())
                .filter(ldapProperties.getGroupSearchFilter(), userDn);

        return ldapTemplate.search(query,
                new ContextMapper<String>() {
                    @Override
                    public String mapFromContext(Object ctx) {
                        return ((DirContextOperations) ctx).getNameInNamespace();
                    }
                }
        );
    }

    /**
     * 根据组 DN 列表映射系统角色（最高权限优先）
     */
    public String mapToRole(List<String> groupDns) {
        System.out.println("===== 角色映射调试 =====");
        System.out.println("配置原始映射表: " + ldapProperties.getRoleMapping());
        System.out.println("LDAP 返回组 DN 列表: " + groupDns);
        Map<String, String> mapping = ldapProperties.getRoleMapping();
        String role = "USER"; // 默认角色

        for (String groupDn : groupDns) {
            String normalizedGroupDn = normalizeDn(groupDn);
            String mappedRole = mapping.get(normalizedGroupDn);
            if (mappedRole == null) continue;
            if ("PRODUCT_ADMIN".equals(mappedRole)) {
                return "PRODUCT_ADMIN";
            }
            if ("EDITOR".equals(mappedRole) && !"PRODUCT_ADMIN".equals(role)) {
                role = "EDITOR";
            }
            // USER 级角色不需要特别赋值，默认已是 USER
        }
        return role;
    }
    private String normalizeDn(String dn) {
        return dn.replaceAll("[=,\\s]", "").toLowerCase();  // 删除所有 = , 和空白字符
    }
    public String findUserDn(String username) {
        LdapContextSource contextSource = createContextSource();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        return ldapTemplate.searchForObject(
                LdapQueryBuilder.query()
                        .filter(ldapProperties.getUserSearchFilter(), username),
                new ContextMapper<String>() {
                    @Override
                    public String mapFromContext(Object ctx) {
                        return ((DirContextOperations) ctx).getNameInNamespace();
                    }
                }
        );
    }
    private LdapContextSource createContextSource() {
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
        return contextSource;
    }
}
