package com.quantcrux.dto;

import com.quantcrux.model.Portfolio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PortfolioResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String userName;
    private BigDecimal totalValue;
    private BigDecimal totalInvestment;
    private BigDecimal totalPnl;
    private BigDecimal pnlPercentage;
    private BigDecimal sharpeRatio;
    private BigDecimal riskScore;
    private Integer positionCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastCalculated;
    private List<TradeDTO> trades;

    // Constructors
    public PortfolioResponseDTO() {}

    public PortfolioResponseDTO(Portfolio portfolio, boolean includeTrades) {
        this.id = portfolio.getId();
        this.name = portfolio.getName();
        this.description = portfolio.getDescription();
        this.userName = portfolio.getUser().getName();
        this.totalValue = portfolio.getTotalValue();
        this.totalInvestment = portfolio.getTotalInvestment();
        this.totalPnl = portfolio.getTotalPnl();
        this.pnlPercentage = portfolio.getPnlPercentage();
        this.sharpeRatio = portfolio.getSharpeRatio();
        this.riskScore = portfolio.getRiskScore();
        this.positionCount = portfolio.getPositionCount();
        this.isActive = portfolio.getIsActive();
        this.createdAt = portfolio.getCreatedAt();
        this.updatedAt = portfolio.getUpdatedAt();
        this.lastCalculated = portfolio.getLastCalculated();
        
        if (includeTrades && portfolio.getTrades() != null) {
            this.trades = portfolio.getTrades().stream()
                    .map(TradeDTO::fromTrade)
                    .collect(Collectors.toList());
        }
    }

    // Static factory methods
    public static PortfolioResponseDTO fromPortfolio(Portfolio portfolio) {
        return new PortfolioResponseDTO(portfolio, false);
    }

    public static PortfolioResponseDTO fromPortfolioWithTrades(Portfolio portfolio) {
        return new PortfolioResponseDTO(portfolio, true);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public BigDecimal getTotalInvestment() { return totalInvestment; }
    public void setTotalInvestment(BigDecimal totalInvestment) { this.totalInvestment = totalInvestment; }

    public BigDecimal getTotalPnl() { return totalPnl; }
    public void setTotalPnl(BigDecimal totalPnl) { this.totalPnl = totalPnl; }

    public BigDecimal getPnlPercentage() { return pnlPercentage; }
    public void setPnlPercentage(BigDecimal pnlPercentage) { this.pnlPercentage = pnlPercentage; }

    public BigDecimal getSharpeRatio() { return sharpeRatio; }
    public void setSharpeRatio(BigDecimal sharpeRatio) { this.sharpeRatio = sharpeRatio; }

    public BigDecimal getRiskScore() { return riskScore; }
    public void setRiskScore(BigDecimal riskScore) { this.riskScore = riskScore; }

    public Integer getPositionCount() { return positionCount; }
    public void setPositionCount(Integer positionCount) { this.positionCount = positionCount; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastCalculated() { return lastCalculated; }
    public void setLastCalculated(LocalDateTime lastCalculated) { this.lastCalculated = lastCalculated; }

    public List<TradeDTO> getTrades() { return trades; }
    public void setTrades(List<TradeDTO> trades) { this.trades = trades; }
}