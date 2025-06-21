package com.quantcrux.dto;

import java.util.List;
import java.util.Map;

public class PortfolioSummary {
    private Map<String, Object> summary;
    private List<Map<String, Object>> positions;

    public PortfolioSummary(Map<String, Object> summary, List<Map<String, Object>> positions) {
        this.summary = summary;
        this.positions = positions;
    }

    // Getters and Setters
    public Map<String, Object> getSummary() { return summary; }
    public void setSummary(Map<String, Object> summary) { this.summary = summary; }

    public List<Map<String, Object>> getPositions() { return positions; }
    public void setPositions(List<Map<String, Object>> positions) { this.positions = positions; }
}