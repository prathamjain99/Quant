package com.quantcrux.service;

import com.quantcrux.dto.UserSummaryResponse;
import com.quantcrux.model.User;
import com.quantcrux.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserSessionRepository sessionRepository;

    @Autowired
    private UserActivityLogRepository activityLogRepository;

    @Autowired
    private UserActivityService userActivityService;

    public UserSummaryResponse getUserSummary(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserSummaryResponse summary = new UserSummaryResponse();

        // Count strategies
        Long strategiesCount = (long) strategyRepository.findByUser(user).size();
        summary.setStrategiesCount(strategiesCount);

        // Count trades
        Long tradesCount = (long) tradeRepository.findByUser(user).size();
        summary.setTradesCount(tradesCount);

        // Count products (only for portfolio managers)
        Long productsCount = 0L;
        if (user.getRole() == User.Role.PORTFOLIO_MANAGER) {
            productsCount = (long) productRepository.findByUser(user).size();
        }
        summary.setProductsCount(productsCount);

        // Mock counts for features not yet implemented
        summary.setBacktestsCount(strategiesCount * 2); // Assume 2 backtests per strategy
        summary.setAnalyticsReportsCount(tradesCount / 2); // Assume 1 report per 2 trades
        summary.setPortfolioSimulationsCount(strategiesCount); // 1 simulation per strategy

        // Get active sessions count
        Long activeSessions = sessionRepository.countByUserAndIsActiveTrue(user);
        summary.setActiveSessions(activeSessions);

        // Calculate total portfolio value (mock calculation)
        Double totalPortfolioValue = calculateTotalPortfolioValue(user);
        summary.setTotalPortfolioValue(totalPortfolioValue);

        // Get last activity
        LocalDateTime lastActivity = getLastActivity(user);
        summary.setLastActivity(lastActivity);

        // Get most used feature
        String mostUsedFeature = userActivityService.getMostUsedFeature(username);
        summary.setMostUsedFeature(mostUsedFeature);

        return summary;
    }

    private Double calculateTotalPortfolioValue(User user) {
        // Mock calculation based on trades
        return tradeRepository.findByUser(user).stream()
                .mapToDouble(trade -> {
                    if (trade.getCurrentPrice() != null && trade.getNotional() != null) {
                        return trade.getCurrentPrice() * trade.getNotional() / 100.0;
                    }
                    return 0.0;
                })
                .sum();
    }

    private LocalDateTime getLastActivity(User user) {
        return activityLogRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .findFirst()
                .map(activity -> activity.getCreatedAt())
                .orElse(user.getLastLogin());
    }
}