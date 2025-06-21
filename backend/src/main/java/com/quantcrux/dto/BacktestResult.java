package com.quantcrux.dto;

import java.util.List;
import java.util.Map;

public class BacktestResult {
    private Map<String, Object> results;
    private List<Map<String, Object>> equityCurve;

    public BacktestResult(Map<String, Object> results, List<Map<String, Object>> equityCurve) {
        this.results = results;
        this.equityCurve = equityCurve;
    }

    // Getters and Setters
    public Map<String, Object> getResults() { return results; }
    public void setResults(Map<String, Object> results) { this.results = results; }

    public List<Map<String, Object>> getEquityCurve() { return equityCurve; }
    public void setEquityCurve(List<Map<String, Object>> equityCurve) { this.equityCurve = equityCurve; }
}