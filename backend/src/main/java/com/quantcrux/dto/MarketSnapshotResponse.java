package com.quantcrux.dto;

import com.quantcrux.model.MarketData;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MarketSnapshotResponse {
    private String symbol;
    private String name;
    private BigDecimal price;
    private BigDecimal changeAmount;
    private BigDecimal changePercent;
    private Long volume;
    private String currency;
    private String exchange;
    private LocalDateTime updatedAt;
    private String trend; // "up", "down", "neutral"

    // Constructors
    public MarketSnapshotResponse() {}

    public MarketSnapshotResponse(MarketData marketData) {
        this.symbol = marketData.getSymbol();
        this.name = marketData.getName();
        this.price = marketData.getPrice();
        this.changeAmount = marketData.getChangeAmount();
        this.changePercent = marketData.getChangePercent();
        this.volume = marketData.getVolume();
        this.currency = marketData.getCurrency();
        this.exchange = marketData.getExchange();
        this.updatedAt = marketData.getUpdatedAt();
        
        // Determine trend
        if (changePercent != null) {
            if (changePercent.compareTo(BigDecimal.ZERO) > 0) {
                this.trend = "up";
            } else if (changePercent.compareTo(BigDecimal.ZERO) < 0) {
                this.trend = "down";
            } else {
                this.trend = "neutral";
            }
        } else {
            this.trend = "neutral";
        }
    }

    // Getters and Setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }

    public BigDecimal getChangePercent() { return changePercent; }
    public void setChangePercent(BigDecimal changePercent) { this.changePercent = changePercent; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }
}