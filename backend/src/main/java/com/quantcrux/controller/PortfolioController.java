package com.quantcrux.controller;

import com.quantcrux.dto.PortfolioSummary;
import com.quantcrux.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<PortfolioSummary> getPortfolio(Authentication authentication) {
        PortfolioSummary portfolio = portfolioService.getPortfolioSummary(authentication.getName());
        return ResponseEntity.ok(portfolio);
    }

    @PostMapping("/positions")
    public ResponseEntity<?> createPosition(@RequestParam Long productId, 
                                          @RequestParam Double quantity,
                                          Authentication authentication) {
        // Implementation for creating positions
        return ResponseEntity.ok().build();
    }
}