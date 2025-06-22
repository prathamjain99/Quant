package com.quantcrux.controller;

import com.quantcrux.dto.ProductDTO;
import com.quantcrux.model.Product;
import com.quantcrux.model.User;
import com.quantcrux.repository.ProductRepository;
import com.quantcrux.repository.UserRepository;
import com.quantcrux.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all products using DTO projection
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        product.setUser(user);
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    /**
     * Get a specific product by ID using DTO projection
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        ProductDTO product = productService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Get products for the authenticated user using DTO projection
     */
    @GetMapping("/my-products")
    public ResponseEntity<List<ProductDTO>> getMyProducts(Authentication authentication) {
        List<ProductDTO> products = productService.getUserProducts(authentication.getName());
        return ResponseEntity.ok(products);
    }

    /**
     * Alternative endpoint using JPQL projection
     */
    @GetMapping("/projection")
    public ResponseEntity<List<ProductDTO>> getAllProductsWithProjection() {
        List<ProductDTO> products = productService.getAllProductsWithProjection();
        return ResponseEntity.ok(products);
    }
}