package com.quantcrux.dto;

import com.quantcrux.model.Product;
import java.time.LocalDateTime;

public class ProductDTO {
    private Long id;
    private String name;
    private String type;
    private String underlyingAsset;
    private Double strike;
    private Double barrier;
    private Double coupon;
    private Double notional;
    private Integer maturityMonths;
    private String issuer;
    private String currency;
    private LocalDateTime createdAt;
    private String createdByUser;

    // Default constructor
    public ProductDTO() {}

    // Constructor for JPQL projection
    public ProductDTO(Long id, String name, String type, String underlyingAsset,
                     Double strike, Double barrier, Double coupon, Double notional,
                     Integer maturityMonths, String issuer, String currency,
                     LocalDateTime createdAt, String createdByUser) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.underlyingAsset = underlyingAsset;
        this.strike = strike;
        this.barrier = barrier;
        this.coupon = coupon;
        this.notional = notional;
        this.maturityMonths = maturityMonths;
        this.issuer = issuer;
        this.currency = currency;
        this.createdAt = createdAt;
        this.createdByUser = createdByUser;
    }

    // Static factory method from Product entity
    public static ProductDTO fromProduct(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setType(product.getType());
        dto.setUnderlyingAsset(product.getUnderlyingAsset());
        dto.setStrike(product.getStrike());
        dto.setBarrier(product.getBarrier());
        dto.setCoupon(product.getCoupon());
        dto.setNotional(product.getNotional());
        dto.setMaturityMonths(product.getMaturityMonths());
        dto.setIssuer(product.getIssuer());
        dto.setCurrency(product.getCurrency());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setCreatedByUser(product.getUser().getName());
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUnderlyingAsset() { return underlyingAsset; }
    public void setUnderlyingAsset(String underlyingAsset) { this.underlyingAsset = underlyingAsset; }

    public Double getStrike() { return strike; }
    public void setStrike(Double strike) { this.strike = strike; }

    public Double getBarrier() { return barrier; }
    public void setBarrier(Double barrier) { this.barrier = barrier; }

    public Double getCoupon() { return coupon; }
    public void setCoupon(Double coupon) { this.coupon = coupon; }

    public Double getNotional() { return notional; }
    public void setNotional(Double notional) { this.notional = notional; }

    public Integer getMaturityMonths() { return maturityMonths; }
    public void setMaturityMonths(Integer maturityMonths) { this.maturityMonths = maturityMonths; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(String createdByUser) { this.createdByUser = createdByUser; }
}