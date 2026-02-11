package org.example.productservice.controller;

/**
 * @author ZSZ
 * @date 2026/2/11 6:47
 * @description
 */
import jakarta.servlet.http.HttpServletRequest;
import org.example.productservice.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public List<ProductMapper.Product> getAllProducts(HttpServletRequest request) {
        return productMapper.findAll();
    }

    @PostMapping
    public ProductMapper.Product createProduct(@RequestBody Map<String, String> request,
                                               HttpServletRequest httpRequest) {
        ProductMapper.Product product = new ProductMapper.Product();
        product.setName(request.get("name"));
        productMapper.insert(product);
        return product;
    }

    @PutMapping("/{id}")
    public ProductMapper.Product updateProduct(@PathVariable Long id,
                                               @RequestBody Map<String, String> request,
                                               HttpServletRequest httpRequest) {
        ProductMapper.Product product = new ProductMapper.Product();
        product.setId(id);
        product.setName(request.get("name"));
        productMapper.update(product);
        return product;
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id,
                                HttpServletRequest httpRequest) {
        productMapper.delete(id);
        return "删除成功";
    }
}