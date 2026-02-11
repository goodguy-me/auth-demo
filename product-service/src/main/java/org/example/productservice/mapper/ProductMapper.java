package org.example.productservice.mapper;

/**
 * @author ZSZ
 * @date 2026/2/11 6:47
 * @description
 */

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT id, name FROM products")
    List<Product> findAll();

    @Insert("INSERT INTO products(name) VALUES(#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE products SET name = #{name} WHERE id = #{id}")
    int update(Product product);

    @Delete("DELETE FROM products WHERE id = #{id}")
    int delete(Long id);

    class Product {
        private Long id;
        private String name;

        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
