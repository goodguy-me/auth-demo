package org.example.authservice.mapper;

/**
 * @author ZSZ
 * @date 2026/2/11 6:40
 * @description
 */
import org.apache.ibatis.annotations.*;
import org.example.authservice.model.entity.User;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, password, role,enabled FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Insert("INSERT INTO users(username,password,role,ldap_dn) " +
            "VALUES(#{username},#{password},#{role},#{ldapDn})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    @Select("SELECT * FROM users WHERE ldap_dn = #{ldapDn}")
    User findByLdapDn(String ldapDn);

    @Update("UPDATE users SET username=#{username} " +
            "WHERE id=#{id}")
    void updateUser(User user);

}
