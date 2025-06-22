package com.quantcrux.repository;

import com.quantcrux.dto.ProductDTO;
import com.quantcrux.model.Product;
import com.quantcrux.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByUser(User user);

    /**
     * Fetch all products with eager loading of User to avoid lazy loading issues
     */
    @Query("SELECT p FROM Product p " +
           "JOIN FETCH p.user u " +
           "ORDER BY p.createdAt DESC")
    List<Product> findAllWithUser();

    /**
     * Fetch products by user with eager loading
     */
    @Query("SELECT p FROM Product p " +
           "JOIN FETCH p.user u " +
           "WHERE p.user = :user " +
           "ORDER BY p.createdAt DESC")
    List<Product> findByUserWithUser(@Param("user") User user);

    /**
     * Fetch a specific product with eager loading
     */
    @Query("SELECT p FROM Product p " +
           "JOIN FETCH p.user u " +
           "WHERE p.id = :productId")
    Optional<Product> findByIdWithUser(@Param("productId") Long productId);

    /**
     * Alternative approach using JPQL projection to create DTOs directly
     */
    @Query("SELECT new com.quantcrux.dto.ProductDTO(" +
           "p.id, p.name, p.type, p.underlyingAsset, p.strike, p.barrier, " +
           "p.coupon, p.notional, p.maturityMonths, p.issuer, p.currency, " +
           "p.createdAt, u.name) " +
           "FROM Product p " +
           "JOIN p.user u " +
           "ORDER BY p.createdAt DESC")
    List<ProductDTO> findAllProductProjections();

    /**
     * Find products by type with eager loading
     */
    @Query("SELECT p FROM Product p " +
           "JOIN FETCH p.user u " +
           "WHERE p.type = :type " +
           "ORDER BY p.createdAt DESC")
    List<Product> findByTypeWithUser(@Param("type") String type);

    /**
     * Find products by underlying asset with eager loading
     */
    @Query("SELECT p FROM Product p " +
           "JOIN FETCH p.user u " +
           "WHERE p.underlyingAsset = :underlyingAsset " +
           "ORDER BY p.createdAt DESC")
    List<Product> findByUnderlyingAssetWithUser(@Param("underlyingAsset") String underlyingAsset);
}