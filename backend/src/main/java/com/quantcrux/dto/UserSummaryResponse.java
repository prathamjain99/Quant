package com.quantcrux.dto;

import java.time.LocalDateTime;

public class UserSummaryResponse {
    private Long strategiesCount;
    private Long backtestsCount;
    private Long analyticsReportsCount;
    private Long portfolioSimulationsCount;
    private Long tradesCount;
    private Long productsCount;
    private LocalDateTime lastActivity;
    private String mostUsedFeature;
    private Double totalPortfolioValue;
    private Long activeSessions;

    // Constructors
    public UserSummaryResponse() {}

    public UserSummaryResponse(Long strategiesCount, Long backtestsCount, Long analyticsReportsCount, 
                              Long portfolioSimulationsCount, Long tradesCount, Long productsCount) {
        this.strategiesCount = strategiesCount;
        this.backtestsCount = backtestsCount;
        this.analyticsReportsCount = analyticsReportsCount;
        this.portfolioSimulationsCount = portfolioSimulationsCount;
        this.tradesCount = tradesCount;
        this.productsCount = productsCount;
    }

    // Getters and Setters
    public Long getStrategiesCount() { return strategiesCount; }
    public void setStrategiesCount(Long strategiesCount) { this.strategiesCount = strategiesCount; }

    public Long getBacktestsCount() { return backtestsCount; }
    public void setBacktestsCount(Long backtestsCount) { this.backtestsCount = backtestsCount; }

    public Long getAnalyticsReportsCount() { return analyticsReportsCount; }
    public void setAnalyticsReportsCount(Long analyticsReportsCount) { this.analyticsReportsCount = analyticsReportsCount; }

    public Long getPortfolioSimulationsCount() { return portfolioSimulationsCount; }
    public void setPortfolioSimulationsCount(Long portfolioSimulationsCount) { this.portfolioSimulationsCount = portfolioSimulationsCount; }

    public Long getTradesCount() { return tradesCount; }
    public void setTradesCount(Long tradesCount) { this.tradesCount = tradesCount; }

    public Long getProductsCount() { return productsCount; }
    public void setProductsCount(Long productsCount) { this.productsCount = productsCount; }

    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }

    public String getMostUsedFeature() { return mostUsedFeature; }
    public void setMostUsedFeature(String mostUsedFeature) { this.mostUsedFeature = mostUsedFeature; }

    public Double getTotalPortfolioValue() { return totalPortfolioValue; }
    public void setTotalPortfolioValue(Double totalPortfolioValue) { this.totalPortfolioValue = totalPortfolioValue; }

    public Long getActiveSessions() { return activeSessions; }
    public void setActiveSessions(Long activeSessions) { this.activeSessions = activeSessions; }
}