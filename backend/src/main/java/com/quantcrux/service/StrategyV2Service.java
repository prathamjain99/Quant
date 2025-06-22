package com.quantcrux.service;

import com.quantcrux.dto.StrategyV2CreateRequest;
import com.quantcrux.dto.StrategyV2DTO;
import com.quantcrux.dto.StrategyV2UpdateRequest;
import com.quantcrux.model.StrategyV2;
import com.quantcrux.model.User;
import com.quantcrux.repository.StrategyV2Repository;
import com.quantcrux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class StrategyV2Service {

    @Autowired
    private StrategyV2Repository strategyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActivityService userActivityService;

    /**
     * Get strategies based on user role and permissions
     */
    @Transactional(readOnly = true)
    public List<StrategyV2DTO> getStrategiesForUser(String username) {
        User user = getUserByUsername(username);
        List<StrategyV2> strategies;

        switch (user.getRole()) {
            case RESEARCHER:
                // Researchers see only their own strategies
                strategies = strategyRepository.findByOwnerWithOwner(user);
                break;
            case PORTFOLIO_MANAGER:
                // Portfolio managers see all strategies
                strategies = strategyRepository.findAllWithOwner();
                break;
            case CLIENT:
                // Clients see only public strategies
                strategies = strategyRepository.findPublicStrategiesWithOwner();
                break;
            default:
                strategies = List.of();
        }

        return strategies.stream()
                .map(strategy -> StrategyV2DTO.fromStrategyWithPermissions(strategy, user))
                .collect(Collectors.toList());
    }

    /**
     * Get a specific strategy by ID
     */
    @Transactional(readOnly = true)
    public StrategyV2DTO getStrategy(String username, Long strategyId) {
        User user = getUserByUsername(username);
        StrategyV2 strategy = strategyRepository.findByIdWithOwner(strategyId)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));

        if (!strategy.canBeViewedBy(user)) {
            throw new RuntimeException("Access denied: You don't have permission to view this strategy");
        }

        return StrategyV2DTO.fromStrategyWithPermissions(strategy, user);
    }

    /**
     * Create a new strategy
     */
    public StrategyV2DTO createStrategy(String username, StrategyV2CreateRequest request) {
        User user = getUserByUsername(username);

        if (!User.Role.RESEARCHER.equals(user.getRole())) {
            throw new RuntimeException("Access denied: Only researchers can create strategies");
        }

        // Check if strategy name already exists for this user
        if (strategyRepository.existsByOwnerAndNameIgnoreCase(user, request.getName())) {
            throw new IllegalArgumentException("A strategy with this name already exists");
        }

        // Create strategy with default code_json if not provided
        Map<String, Object> codeJson = request.getCodeJson();
        if (codeJson == null || codeJson.isEmpty()) {
            codeJson = createDefaultCodeJson();
        }

        StrategyV2 strategy = new StrategyV2(request.getName(), request.getDescription(), codeJson, user);
        strategy.setTags(request.getTags());

        StrategyV2 savedStrategy = strategyRepository.save(strategy);

        // Log activity
        userActivityService.logActivity(
                username,
                "STRATEGY_CREATED",
                "Created strategy: " + request.getName(),
                "StrategyV2",
                savedStrategy.getId()
        );

        return StrategyV2DTO.fromStrategyWithPermissions(savedStrategy, user);
    }

    /**
     * Update an existing strategy
     */
    public StrategyV2DTO updateStrategy(String username, Long strategyId, StrategyV2UpdateRequest request) {
        User user = getUserByUsername(username);
        StrategyV2 strategy = strategyRepository.findByIdWithOwner(strategyId)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));

        if (!strategy.canBeModifiedBy(user)) {
            throw new RuntimeException("Access denied: You can only modify your own strategies");
        }

        // Check if new name conflicts with existing strategies (excluding current one)
        if (!strategy.getName().equalsIgnoreCase(request.getName()) &&
                strategyRepository.existsByOwnerAndNameIgnoreCaseAndIdNot(user, request.getName(), strategyId)) {
            throw new IllegalArgumentException("A strategy with this name already exists");
        }

        String oldName = strategy.getName();
        strategy.setName(request.getName());
        strategy.setDescription(request.getDescription());
        strategy.setCodeJson(request.getCodeJson());
        strategy.setTags(request.getTags());

        StrategyV2 savedStrategy = strategyRepository.save(strategy);

        // Log activity
        userActivityService.logActivity(
                username,
                "STRATEGY_UPDATED",
                "Updated strategy: " + oldName + " -> " + request.getName(),
                "StrategyV2",
                savedStrategy.getId()
        );

        return StrategyV2DTO.fromStrategyWithPermissions(savedStrategy, user);
    }

    /**
     * Delete a strategy
     */
    public void deleteStrategy(String username, Long strategyId) {
        User user = getUserByUsername(username);
        StrategyV2 strategy = strategyRepository.findByIdWithOwner(strategyId)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));

        if (!strategy.canBeModifiedBy(user)) {
            throw new RuntimeException("Access denied: You can only delete your own strategies");
        }

        String strategyName = strategy.getName();
        strategyRepository.delete(strategy);

        // Log activity
        userActivityService.logActivity(
                username,
                "STRATEGY_DELETED",
                "Deleted strategy: " + strategyName,
                "StrategyV2",
                strategyId
        );
    }

    /**
     * Publish a strategy (make it public)
     */
    public StrategyV2DTO publishStrategy(String username, Long strategyId) {
        User user = getUserByUsername(username);
        StrategyV2 strategy = strategyRepository.findByIdWithOwner(strategyId)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));

        if (!strategy.canBeModifiedBy(user)) {
            throw new RuntimeException("Access denied: You can only publish your own strategies");
        }

        if (strategy.getIsPublic()) {
            throw new IllegalArgumentException("Strategy is already public");
        }

        strategy.publish();
        StrategyV2 savedStrategy = strategyRepository.save(strategy);

        // Log activity
        userActivityService.logActivity(
                username,
                "STRATEGY_PUBLISHED",
                "Published strategy: " + strategy.getName(),
                "StrategyV2",
                savedStrategy.getId()
        );

        return StrategyV2DTO.fromStrategyWithPermissions(savedStrategy, user);
    }

    /**
     * Unpublish a strategy (make it private)
     */
    public StrategyV2DTO unpublishStrategy(String username, Long strategyId) {
        User user = getUserByUsername(username);
        StrategyV2 strategy = strategyRepository.findByIdWithOwner(strategyId)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));

        if (!strategy.canBeModifiedBy(user)) {
            throw new RuntimeException("Access denied: You can only unpublish your own strategies");
        }

        if (!strategy.getIsPublic()) {
            throw new IllegalArgumentException("Strategy is already private");
        }

        strategy.unpublish();
        StrategyV2 savedStrategy = strategyRepository.save(strategy);

        // Log activity
        userActivityService.logActivity(
                username,
                "STRATEGY_UNPUBLISHED",
                "Unpublished strategy: " + strategy.getName(),
                "StrategyV2",
                savedStrategy.getId()
        );

        return StrategyV2DTO.fromStrategyWithPermissions(savedStrategy, user);
    }

    /**
     * Search strategies by name pattern
     */
    @Transactional(readOnly = true)
    public List<StrategyV2DTO> searchStrategies(String username, String searchTerm) {
        User user = getUserByUsername(username);
        List<StrategyV2> strategies;

        switch (user.getRole()) {
            case RESEARCHER:
                strategies = strategyRepository.findByOwnerAndNameContainingIgnoreCase(user, searchTerm);
                break;
            case PORTFOLIO_MANAGER:
                // For portfolio managers, search all strategies (this would need a custom query)
                strategies = strategyRepository.findAllWithOwner().stream()
                        .filter(s -> s.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                        .collect(Collectors.toList());
                break;
            case CLIENT:
                strategies = strategyRepository.findPublicStrategiesByNameContainingIgnoreCase(searchTerm);
                break;
            default:
                strategies = List.of();
        }

        return strategies.stream()
                .map(strategy -> StrategyV2DTO.fromStrategyWithPermissions(strategy, user))
                .collect(Collectors.toList());
    }

    /**
     * Get strategy statistics for a user
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStrategyStatistics(String username) {
        User user = getUserByUsername(username);
        Object[] stats = strategyRepository.getStrategyStatsByOwner(user);

        Map<String, Object> result = new HashMap<>();
        if (stats != null && stats.length >= 3) {
            result.put("totalStrategies", stats[0]);
            result.put("publicStrategies", stats[1]);
            result.put("privateStrategies", stats[2]);
        } else {
            result.put("totalStrategies", 0);
            result.put("publicStrategies", 0);
            result.put("privateStrategies", 0);
        }

        return result;
    }

    // Private helper methods

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Map<String, Object> createDefaultCodeJson() {
        Map<String, Object> defaultCode = new HashMap<>();
        
        Map<String, Object> indicators = new HashMap<>();
        indicators.put("sma_short", 20);
        indicators.put("sma_long", 50);
        indicators.put("rsi_period", 14);
        
        Map<String, Object> entryConditions = new HashMap<>();
        entryConditions.put("price_above_sma", true);
        entryConditions.put("rsi_above", 50);
        
        Map<String, Object> exitConditions = new HashMap<>();
        exitConditions.put("stop_loss_percent", 5);
        exitConditions.put("take_profit_percent", 10);
        
        Map<String, Object> riskManagement = new HashMap<>();
        riskManagement.put("max_position_size", 0.1);
        riskManagement.put("max_drawdown", 0.15);
        
        defaultCode.put("indicators", indicators);
        defaultCode.put("entry_conditions", entryConditions);
        defaultCode.put("exit_conditions", exitConditions);
        defaultCode.put("risk_management", riskManagement);
        
        return defaultCode;
    }
}