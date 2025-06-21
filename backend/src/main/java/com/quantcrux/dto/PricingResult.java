package com.quantcrux.dto;

import java.util.Map;

public class PricingResult {
    private Double price;
    private Map<String, Double> greeks;
    private Double confidenceInterval;
    private Integer numSimulations;

    public PricingResult(Double price, Map<String, Double> greeks, Double confidenceInterval, Integer numSimulations) {
        this.price = price;
        this.greeks = greeks;
        this.confidenceInterval = confidenceInterval;
        this.numSimulations = numSimulations;
    }

    // Getters and Setters
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Map<String, Double> getGreeks() { return greeks; }
    public void setGreeks(Map<String, Double> greeks) { this.greeks = greeks; }

    public Double getConfidenceInterval() { return confidenceInterval; }
    public void setConfidenceInterval(Double confidenceInterval) { this.confidenceInterval = confidenceInterval; }

    public Integer getNumSimulations() { return numSimulations; }
    public void setNumSimulations(Integer numSimulations) { this.numSimulations = numSimulations; }
}