package com.quantcrux.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_data")
public class MarketData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", precision = 15, scale = 6, nullable = false)
    private BigDecimal price;

    @Column(name = "previous_close", precision = 15, scale = 6)
    private BigDecimal previousClose;

    @Column(name = "change_amount", precision = 15, scale = 6)
    private BigDecimal changeAmount;

    @Column(name = "change_percent", precision = 8, scale = 4)
    private BigDecimal changePercent;

    @Column(name = "volume")
    private Long volume;

    @Column(name = "market_cap")
    private Long marketCap;

    @Column(name = "high_52w", precision = 15, scale = 6)
    private BigDecimal high52w;

    @Column(name = "low_52w", precision = 15, scale = 6)
    private BigDecimal low52w;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "exchange")
    private String exchange;

    @Column(name = "sector")
    private String sector;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public MarketData() {}

    public MarketData(String symbol, String name, BigDecimal price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getPreviousClose() { return previousClose; }
    public void setPreviousClose(BigDecimal previousClose) { this.previousClose = previousClose; }

    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }

    public BigDecimal getChangePercent() { return changePercent; }
    public void setChangePercent(BigDecimal changePercent) { this.changePercent = changePercent; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public Long getMarketCap() { return marketCap; }
    public void setMarketCap(Long marketCap) { this.marketCap = marketCap; }

    public BigDecimal getHigh52w() { return high52w; }
    public void setHigh52w(BigDecimal high52w) { this.high52w = high52w; }

    public BigDecimal getLow52w() { return low52w; }
    public void setLow52w(BigDecimal low52w) { this.low52w = low52w; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}