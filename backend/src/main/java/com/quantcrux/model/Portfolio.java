package com.quantcrux.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolios")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Portfolio name is required")
    @Size(min = 2, max = 100, message = "Portfolio name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Trade> trades = new ArrayList<>();

    // Computed metrics (calculated and stored for performance)
    @Column(name = "total_value", precision = 15, scale = 2)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Column(name = "total_investment", precision = 15, scale = 2)
    private BigDecimal totalInvestment = BigDecimal.ZERO;

    @Column(name = "total_pnl", precision = 15, scale = 2)
    private BigDecimal totalPnl = BigDecimal.ZERO;

    @Column(name = "pnl_percentage", precision = 8, scale = 4)
    private BigDecimal pnlPercentage = BigDecimal.ZERO;

    @Column(name = "sharpe_ratio", precision = 8, scale = 4)
    private BigDecimal sharpeRatio = BigDecimal.ZERO;

    @Column(name = "risk_score", precision = 5, scale = 2)
    private BigDecimal riskScore = BigDecimal.ZERO;

    @Column(name = "position_count")
    private Integer positionCount = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_calculated")
    private LocalDateTime lastCalculated;

    // Constructors
    public Portfolio() {}

    public Portfolio(String name, String description, User user) {
        this.name = name;
        this.description = description;
        this.user = user;
    }

    // Helper methods
    public void addTrade(Trade trade) {
        trades.add(trade);
        trade.setPortfolio(this);
    }

    public void removeTrade(Trade trade) {
        trades.remove(trade);
        trade.setPortfolio(null);
    }

    public void updateMetrics(BigDecimal totalValue, BigDecimal totalInvestment, 
                             BigDecimal sharpeRatio, BigDecimal riskScore) {
        this.totalValue = totalValue;
        this.totalInvestment = totalInvestment;
        this.totalPnl = totalValue.subtract(totalInvestment);
        this.pnlPercentage = totalInvestment.compareTo(BigDecimal.ZERO) > 0 
            ? totalPnl.divide(totalInvestment, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;
        this.sharpeRatio = sharpeRatio;
        this.riskScore = riskScore;
        this.positionCount = trades.size();
        this.lastCalculated = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Trade> getTrades() { return trades; }
    public void setTrades(List<Trade> trades) { this.trades = trades; }

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
}