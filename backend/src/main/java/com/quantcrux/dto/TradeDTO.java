package com.quantcrux.dto;

import com.quantcrux.model.Trade;
import java.time.LocalDateTime;

public class TradeDTO {
    private Long id;
    private String productName;
    private String productType;
    private String underlyingAsset;
    private String tradeType;
    private String status;
    private Double notional;
    private Double entryPrice;
    private Double currentPrice;
    private Double pnl;
    private String notes;
    private LocalDateTime tradeDate;
    private String userName;

    // Default constructor
    public TradeDTO() {}

    // Constructor for easy mapping
    public TradeDTO(Long id, String productName, String productType, String underlyingAsset,
                   String tradeType, String status, Double notional, Double entryPrice,
                   Double currentPrice, String notes, LocalDateTime tradeDate, String userName) {
        this.id = id;
        this.productName = productName;
        this.productType = productType;
        this.underlyingAsset = underlyingAsset;
        this.tradeType = tradeType;
        this.status = status;
        this.notional = notional;
        this.entryPrice = entryPrice;
        this.currentPrice = currentPrice;
        this.notes = notes;
        this.tradeDate = tradeDate;
        this.userName = userName;
        
        // Calculate P&L
        this.pnl = calculatePnL(tradeType, notional, entryPrice, currentPrice);
    }

    // Static factory method from Trade entity
    public static TradeDTO fromTrade(Trade trade) {
        TradeDTO dto = new TradeDTO();
        dto.setId(trade.getId());
        dto.setProductName(trade.getProduct().getName());
        dto.setProductType(trade.getProduct().getType());
        dto.setUnderlyingAsset(trade.getProduct().getUnderlyingAsset());
        dto.setTradeType(trade.getTradeType());
        dto.setStatus(trade.getStatus().name());
        dto.setNotional(trade.getNotional());
        dto.setEntryPrice(trade.getEntryPrice());
        dto.setCurrentPrice(trade.getCurrentPrice());
        dto.setNotes(trade.getNotes());
        dto.setTradeDate(trade.getTradeDate());
        dto.setUserName(trade.getUser().getName());
        dto.setPnl(calculatePnL(trade.getTradeType(), trade.getNotional(), 
                               trade.getEntryPrice(), trade.getCurrentPrice()));
        return dto;
    }

    private static Double calculatePnL(String tradeType, Double notional, Double entryPrice, Double currentPrice) {
        if (entryPrice == null || currentPrice == null || notional == null) {
            return 0.0;
        }
        
        double pnl = 0.0;
        if ("BUY".equals(tradeType)) {
            pnl = (currentPrice - entryPrice) * notional / 100;
        } else if ("SELL".equals(tradeType)) {
            pnl = (entryPrice - currentPrice) * notional / 100;
        }
        return pnl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getUnderlyingAsset() { return underlyingAsset; }
    public void setUnderlyingAsset(String underlyingAsset) { this.underlyingAsset = underlyingAsset; }

    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getNotional() { return notional; }
    public void setNotional(Double notional) { this.notional = notional; }

    public Double getEntryPrice() { return entryPrice; }
    public void setEntryPrice(Double entryPrice) { this.entryPrice = entryPrice; }

    public Double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(Double currentPrice) { this.currentPrice = currentPrice; }

    public Double getPnl() { return pnl; }
    public void setPnl(Double pnl) { this.pnl = pnl; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDateTime tradeDate) { this.tradeDate = tradeDate; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}