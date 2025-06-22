package com.quantcrux.service;

import com.quantcrux.dto.StrategyDTO;
import com.quantcrux.model.Strategy;
import com.quantcrux.model.User;
import com.quantcrux.repository.StrategyRepository;
import com.quantcrux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StrategyService {

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all strategies for a user with proper DTO projection
     */
    public List<StrategyDTO> getUserStrategies(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Strategy> strategies = strategyRepository.findByUserWithUser(user);
        return strategies.stream()
                .map(StrategyDTO::fromStrategy)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific strategy by ID
     */
    public StrategyDTO getStrategy(String username, Long strategyId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Strategy strategy = strategyRepository.findByIdAndUserWithUser(strategyId, user)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));

        return StrategyDTO.fromStrategy(strategy);
    }
}