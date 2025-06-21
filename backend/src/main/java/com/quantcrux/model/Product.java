package com.quantcrux.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @Column(name = "underlying_asset")
    private String underlyingAsset;

    @NotNull
    private Double strike;

    private Double barrier;

    @NotNull
    private Double coupon;

    @NotNull
    private Double notional;

    @Column(name = "maturity_months")
    private Integer maturityMonths;

    private String issuer;

    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}