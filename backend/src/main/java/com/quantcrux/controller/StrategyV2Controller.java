package com.quantcrux.controller;

import com.quantcrux.dto.MessageResponse;
import com.quantcrux.dto.StrategyV2CreateRequest;
import com.quantcrux.dto.StrategyV2DTO;
import com.quantcrux.dto.StrategyV2UpdateRequest;
import com.quantcrux.service.StrategyV2Service;
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
@RequestMapping("/api/strategies-v2")
public class StrategyV2Controller {

    @Autowired
    private StrategyV2Service strategyService;

    /**
     * Get all strategies based on user role and permissions
     */
    @GetMapping
    public ResponseEntity<?> getStrategies(Authentication authentication,
                                         @RequestParam(required = false) String search) {
        try {
            List<StrategyV2DTO> strategies;
            
            if (search != null && !search.trim().isEmpty()) {
                strategies = strategyService.searchStrategies(authentication.getName(), search.trim());
            } else {
                strategies = strategyService.getStrategiesForUser(authentication.getName());
            }
            
            return ResponseEntity.ok(strategies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to fetch strategies: " + e.getMessage()));
        }
    }

    /**
     * Get a specific strategy by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStrategy(@PathVariable Long id,
                                       Authentication authentication) {
        try {
            StrategyV2DTO strategy = strategyService.getStrategy(authentication.getName(), id);
            return ResponseEntity.ok(strategy);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Strategy not found"));
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Access denied"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to fetch strategy: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to fetch strategy: " + e.getMessage()));
        }
    }

    /**
     * Create a new strategy (Researcher only)
     */
    @PostMapping
    public ResponseEntity<?> createStrategy(@Valid @RequestBody StrategyV2CreateRequest request,
                                          Authentication authentication) {
        try {
            StrategyV2DTO strategy = strategyService.createStrategy(authentication.getName(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(strategy);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Access denied: Only researchers can create strategies"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to create strategy: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to create strategy: " + e.getMessage()));
        }
    }

    /**
     * Update an existing strategy (Researcher only, own strategies)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStrategy(@PathVariable Long id,
                                          @Valid @RequestBody StrategyV2UpdateRequest request,
                                          Authentication authentication) {
        try {
            StrategyV2DTO strategy = strategyService.updateStrategy(authentication.getName(), id, request);
            return ResponseEntity.ok(strategy);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Strategy not found"));
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Access denied"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to update strategy: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to update strategy: " + e.getMessage()));
        }
    }

    /**
     * Delete a strategy (Researcher only, own strategies)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStrategy(@PathVariable Long id,
                                          Authentication authentication) {
        try {
            strategyService.deleteStrategy(authentication.getName(), id);
            return ResponseEntity.ok(new MessageResponse("Strategy deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Strategy not found"));
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Access denied"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete strategy: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete strategy: " + e.getMessage()));
        }
    }

    /**
     * Publish a strategy (make it public) - Researcher only, own strategies
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishStrategy(@PathVariable Long id,
                                           Authentication authentication) {
        try {
            StrategyV2DTO strategy = strategyService.publishStrategy(authentication.getName(), id);
            return ResponseEntity.ok(strategy);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Strategy not found"));
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Access denied"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to publish strategy: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to publish strategy: " + e.getMessage()));
        }
    }

    /**
     * Unpublish a strategy (make it private) - Researcher only, own strategies
     */
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<?> unpublishStrategy(@PathVariable Long id,
                                             Authentication authentication) {
        try {
            StrategyV2DTO strategy = strategyService.unpublishStrategy(authentication.getName(), id);
            return ResponseEntity.ok(strategy);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Strategy not found"));
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Access denied"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to unpublish strategy: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to unpublish strategy: " + e.getMessage()));
        }
    }

    /**
     * Get strategy statistics for the current user
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStrategyStatistics(Authentication authentication) {
        try {
            Map<String, Object> statistics = strategyService.getStrategyStatistics(authentication.getName());
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to fetch strategy statistics: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "strategy-management-v2");
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }
}