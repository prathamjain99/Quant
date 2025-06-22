package com.quantcrux.controller;

import com.quantcrux.dto.RiskMetrics;
import com.quantcrux.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// @CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, maxAge = 3600)
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/risk-metrics")
    public ResponseEntity<RiskMetrics> getRiskMetrics(Authentication authentication) {
        RiskMetrics metrics = analyticsService.calculateRiskMetrics(authentication.getName());
        return ResponseEntity.ok(metrics);
    }
}