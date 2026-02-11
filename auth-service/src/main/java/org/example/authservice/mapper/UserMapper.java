package org.example.authservice.mapper;

/**
 * @author ZSZ
 * @date 2026/2/11 6:40
 * @description
 */
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, password, role,enabled FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Insert("INSERT INTO users(username,password,role,ldap_dn) " +
            "VALUES(#{username},#{password},#{role}),#{ldapDn}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    @Update("UPDATE users SET username=#{username} " +
            "WHERE id=#{id}")

    @Select("SELECT * FROM users WHERE ldap_dn = #{ldapDn}")
    User findByLdapDn(String ldapDn);
    void updateUser(User user);
    class User {
        private Long id;
        private String username;
        private String password;
        private String role;
        private boolean enabled;
        private String ldapDn;

        public String getLdapDn() {
            return ldapDn;
        }

        public void setLdapDn(String ldapDn) {
            this.ldapDn = ldapDn;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
