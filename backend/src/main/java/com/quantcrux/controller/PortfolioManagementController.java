package com.quantcrux.controller;

import com.quantcrux.dto.MessageResponse;
import com.quantcrux.dto.PortfolioCreateRequest;
import com.quantcrux.dto.PortfolioResponseDTO;
import com.quantcrux.dto.PortfolioUpdateRequest;
import com.quantcrux.service.PortfolioManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, maxAge = 3600)
@RestController
@RequestMapping("/api/portfolios")
public class PortfolioManagementController {

    @Autowired
    private PortfolioManagementService portfolioManagementService;

    /**
     * Create a new portfolio
     */
    @PostMapping
    public ResponseEntity<?> createPortfolio(@Valid @RequestBody PortfolioCreateRequest request,
                                           Authentication authentication) {
        try {
            PortfolioResponseDTO portfolio = portfolioManagementService.createPortfolio(
                    authentication.getName(), request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(portfolio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to create portfolio: " + e.getMessage()));
        }
    }

    /**
     * Get all portfolios for the current user
     */
    @GetMapping
    public ResponseEntity<?> getUserPortfolios(Authentication authentication,
                                             @RequestParam(required = false) String search) {
        try {
            List<PortfolioResponseDTO> portfolios;
            
            if (search != null && !search.trim().isEmpty()) {
                portfolios = portfolioManagementService.searchPortfolios(
                        authentication.getName(), search.trim());
            } else {
                portfolios = portfolioManagementService.getUserPortfolios(authentication.getName());
            }
            
            return ResponseEntity.ok(portfolios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to fetch portfolios: " + e.getMessage()));
        }
    }

    /**
     * Get details of a specific portfolio
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPortfolioDetails(@PathVariable Long id,
                                               Authentication authentication) {
        try {
            PortfolioResponseDTO portfolio = portfolioManagementService.getPortfolioDetails(
                    authentication.getName(), id);
            
            return ResponseEntity.ok(portfolio);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found") || e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Portfolio not found or access denied"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to fetch portfolio: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to fetch portfolio: " + e.getMessage()));
        }
    }

    /**
     * Update portfolio metadata
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePortfolio(@PathVariable Long id,
                                           @Valid @RequestBody PortfolioUpdateRequest request,
                                           Authentication authentication) {
        try {
            PortfolioResponseDTO portfolio = portfolioManagementService.updatePortfolio(
                    authentication.getName(), id, request);
            
            return ResponseEntity.ok(portfolio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found") || e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Portfolio not found or access denied"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to update portfolio: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to update portfolio: " + e.getMessage()));
        }
    }

    /**
     * Delete a portfolio
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePortfolio(@PathVariable Long id,
                                           Authentication authentication) {
        try {
            portfolioManagementService.deletePortfolio(authentication.getName(), id);
            
            return ResponseEntity.ok(new MessageResponse("Portfolio deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found") || e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Portfolio not found or access denied"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete portfolio: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete portfolio: " + e.getMessage()));
        }
    }

    /**
     * Get portfolio statistics for the current user
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getPortfolioStatistics(Authentication authentication) {
        try {
            Object[] stats = portfolioManagementService.getPortfolioStatistics(authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            if (stats != null && stats.length >= 4) {
                response.put("totalPortfolios", stats[0]);
                response.put("totalValue", stats[1]);
                response.put("avgPnlPercentage", stats[2]);
                response.put("totalPositions", stats[3]);
            } else {
                response.put("totalPortfolios", 0);
                response.put("totalValue", 0);
                response.put("avgPnlPercentage", 0);
                response.put("totalPositions", 0);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to fetch portfolio statistics: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "portfolio-management");
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }
}