package com.quantcrux.service;

import com.quantcrux.dto.ProductDTO;
import com.quantcrux.model.Product;
import com.quantcrux.model.User;
import com.quantcrux.repository.ProductRepository;
import com.quantcrux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all products with proper DTO projection
     */
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAllWithUser();
        return products.stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }

    /**
     * Get products for a specific user
     */
    public List<ProductDTO> getUserProducts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Product> products = productRepository.findByUserWithUser(user);
        return products.stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific product by ID
     */
    public ProductDTO getProduct(Long productId) {
        Product product = productRepository.findByIdWithUser(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductDTO.fromProduct(product);
    }

    /**
     * Get products using JPQL projection (alternative approach)
     */
    public List<ProductDTO> getAllProductsWithProjection() {
        return productRepository.findAllProductProjections();
    }
}