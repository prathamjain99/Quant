package com.quantcrux.controller;

import com.quantcrux.dto.BacktestRequest;
import com.quantcrux.dto.BacktestResult;
import com.quantcrux.dto.StrategyDTO;
import com.quantcrux.model.Strategy;
import com.quantcrux.model.User;
import com.quantcrux.repository.StrategyRepository;
import com.quantcrux.repository.UserRepository;
import com.quantcrux.service.BacktestService;
import com.quantcrux.service.StrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/strategies")
public class StrategyController {

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BacktestService backtestService;

    /**
     * Get all strategies for the authenticated user using DTO projection
     */
    @GetMapping
    public ResponseEntity<List<StrategyDTO>> getAllStrategies(Authentication authentication) {
        List<StrategyDTO> strategies = strategyService.getUserStrategies(authentication.getName());
        return ResponseEntity.ok(strategies);
    }

    @PostMapping
    public ResponseEntity<Strategy> createStrategy(@Valid @RequestBody Strategy strategy, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        strategy.setUser(user);
        Strategy savedStrategy = strategyRepository.save(strategy);
        return ResponseEntity.ok(savedStrategy);
    }

    /**
     * Get a specific strategy by ID using DTO projection
     */
    @GetMapping("/{id}")
    public ResponseEntity<StrategyDTO> getStrategy(@PathVariable Long id, Authentication authentication) {
        StrategyDTO strategy = strategyService.getStrategy(authentication.getName(), id);
        return ResponseEntity.ok(strategy);
    }
}