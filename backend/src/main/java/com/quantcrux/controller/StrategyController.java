package com.quantcrux.controller;

import com.quantcrux.dto.BacktestRequest;
import com.quantcrux.dto.BacktestResult;
import com.quantcrux.model.Strategy;
import com.quantcrux.model.User;
import com.quantcrux.repository.StrategyRepository;
import com.quantcrux.repository.UserRepository;
import com.quantcrux.service.BacktestService;
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
    private StrategyRepository strategyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BacktestService backtestService;

    @GetMapping
    public ResponseEntity<List<Strategy>> getAllStrategies(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Strategy> strategies = strategyRepository.findByUser(user);
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

    @GetMapping("/{id}")
    public ResponseEntity<Strategy> getStrategy(@PathVariable Long id, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));
        
        if (!strategy.getUser().getId().equals(user.getId())) {
            return ResponseEntity.forbidden().build();
        }
        
        return ResponseEntity.ok(strategy);
    }
}