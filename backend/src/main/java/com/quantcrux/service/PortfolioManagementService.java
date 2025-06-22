package com.quantcrux.service;

import com.quantcrux.dto.PortfolioCreateRequest;
import com.quantcrux.dto.PortfolioResponseDTO;
import com.quantcrux.dto.PortfolioUpdateRequest;
import com.quantcrux.model.Portfolio;
import com.quantcrux.model.Trade;
import com.quantcrux.model.User;
import com.quantcrux.repository.PortfolioRepository;
import com.quantcrux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class PortfolioManagementService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActivityService userActivityService;

    private final Random random = new Random();

    /**
     * Create a new portfolio for the authenticated user
     */
    public PortfolioResponseDTO createPortfolio(String username, PortfolioCreateRequest request) {
        User user = getUserByUsername(username);
        
        // Check if portfolio name already exists for this user
        if (portfolioRepository.existsByUserAndNameIgnoreCase(user, request.getName())) {
            throw new IllegalArgumentException("A portfolio with this name already exists");
        }

        Portfolio portfolio = new Portfolio(request.getName(), request.getDescription(), user);
        
        // Initialize with default metrics
        portfolio.updateMetrics(
            BigDecimal.ZERO, 
            BigDecimal.ZERO, 
            BigDecimal.ZERO, 
            BigDecimal.valueOf(50.0) // Default risk score
        );

        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        
        // Log activity
        userActivityService.logActivity(
            username, 
            "PORTFOLIO_CREATED", 
            "Created portfolio: " + request.getName(),
            "Portfolio",
            savedPortfolio.getId()
        );

        return PortfolioResponseDTO.fromPortfolio(savedPortfolio);
    }

    /**
     * Get all portfolios for the authenticated user
     */
    @Transactional(readOnly = true)
    public List<PortfolioResponseDTO> getUserPortfolios(String username) {
        User user = getUserByUsername(username);
        
        List<Portfolio> portfolios = portfolioRepository.findByUserAndIsActiveTrueOrderByUpdatedAtDesc(user);
        
        return portfolios.stream()
                .map(PortfolioResponseDTO::fromPortfolio)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific portfolio with detailed information including trades
     */
    @Transactional(readOnly = true)
    public PortfolioResponseDTO getPortfolioDetails(String username, Long portfolioId) {
        User user = getUserByUsername(username);
        
        Portfolio portfolio = portfolioRepository.findByIdAndUserWithTrades(portfolioId, user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found or access denied"));

        // Recalculate metrics if needed
        if (shouldRecalculateMetrics(portfolio)) {
            recalculatePortfolioMetrics(portfolio);
            portfolioRepository.save(portfolio);
        }

        return PortfolioResponseDTO.fromPortfolioWithTrades(portfolio);
    }

    /**
     * Update portfolio metadata (name and description)
     */
    public PortfolioResponseDTO updatePortfolio(String username, Long portfolioId, PortfolioUpdateRequest request) {
        User user = getUserByUsername(username);
        
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found or access denied"));

        // Check if new name conflicts with existing portfolios (excluding current one)
        if (!portfolio.getName().equalsIgnoreCase(request.getName()) &&
            portfolioRepository.existsByUserAndNameIgnoreCaseAndIdNot(user, request.getName(), portfolioId)) {
            throw new IllegalArgumentException("A portfolio with this name already exists");
        }

        String oldName = portfolio.getName();
        portfolio.setName(request.getName());
        portfolio.setDescription(request.getDescription());

        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        
        // Log activity
        userActivityService.logActivity(
            username, 
            "PORTFOLIO_UPDATED", 
            "Updated portfolio: " + oldName + " -> " + request.getName(),
            "Portfolio",
            savedPortfolio.getId()
        );

        return PortfolioResponseDTO.fromPortfolio(savedPortfolio);
    }

    /**
     * Delete a portfolio and all associated trades
     */
    public void deletePortfolio(String username, Long portfolioId) {
        User user = getUserByUsername(username);
        
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found or access denied"));

        String portfolioName = portfolio.getName();
        int tradeCount = portfolio.getTrades().size();

        // Soft delete - mark as inactive
        portfolio.setIsActive(false);
        portfolioRepository.save(portfolio);
        
        // Log activity
        userActivityService.logActivity(
            username, 
            "PORTFOLIO_DELETED", 
            "Deleted portfolio: " + portfolioName + " (contained " + tradeCount + " trades)",
            "Portfolio",
            portfolioId
        );
    }

    /**
     * Recalculate portfolio metrics based on current trades
     */
    public void recalculatePortfolioMetrics(Portfolio portfolio) {
        List<Trade> activeTrades = portfolio.getTrades().stream()
                .filter(trade -> trade.getStatus() == Trade.TradeStatus.CONFIRMED || 
                               trade.getStatus() == Trade.TradeStatus.SETTLED)
                .collect(Collectors.toList());

        BigDecimal totalInvestment = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;

        for (Trade trade : activeTrades) {
            if (trade.getNotional() != null && trade.getEntryPrice() != null) {
                BigDecimal investment = BigDecimal.valueOf(trade.getNotional())
                        .multiply(BigDecimal.valueOf(trade.getEntryPrice()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                totalInvestment = totalInvestment.add(investment);

                if (trade.getCurrentPrice() != null) {
                    BigDecimal currentValue = BigDecimal.valueOf(trade.getNotional())
                            .multiply(BigDecimal.valueOf(trade.getCurrentPrice()))
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    totalCurrentValue = totalCurrentValue.add(currentValue);
                } else {
                    totalCurrentValue = totalCurrentValue.add(investment);
                }
            }
        }

        // Calculate Sharpe ratio (simplified mock calculation)
        BigDecimal sharpeRatio = calculateMockSharpeRatio(totalInvestment, totalCurrentValue);
        
        // Calculate risk score (simplified mock calculation)
        BigDecimal riskScore = calculateMockRiskScore(activeTrades.size(), totalCurrentValue);

        portfolio.updateMetrics(totalCurrentValue, totalInvestment, sharpeRatio, riskScore);
    }

    /**
     * Get portfolio statistics for a user
     */
    @Transactional(readOnly = true)
    public Object[] getPortfolioStatistics(String username) {
        User user = getUserByUsername(username);
        return portfolioRepository.getPortfolioStatsByUser(user);
    }

    /**
     * Search portfolios by name pattern
     */
    @Transactional(readOnly = true)
    public List<PortfolioResponseDTO> searchPortfolios(String username, String namePattern) {
        User user = getUserByUsername(username);
        
        List<Portfolio> portfolios = portfolioRepository.findByUserAndNameContainingIgnoreCase(user, namePattern);
        
        return portfolios.stream()
                .map(PortfolioResponseDTO::fromPortfolio)
                .collect(Collectors.toList());
    }

    // Private helper methods

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean shouldRecalculateMetrics(Portfolio portfolio) {
        if (portfolio.getLastCalculated() == null) {
            return true;
        }
        
        // Recalculate if metrics are older than 1 hour
        LocalDateTime threshold = LocalDateTime.now().minusHours(1);
        return portfolio.getLastCalculated().isBefore(threshold);
    }

    private BigDecimal calculateMockSharpeRatio(BigDecimal totalInvestment, BigDecimal totalCurrentValue) {
        if (totalInvestment.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Mock calculation: base Sharpe ratio on performance with some randomness
        BigDecimal returnRate = totalCurrentValue.subtract(totalInvestment)
                .divide(totalInvestment, 4, RoundingMode.HALF_UP);
        
        // Add some realistic variance
        double baseRatio = returnRate.doubleValue() * 2.0; // Simplified calculation
        double variance = (random.nextGaussian() * 0.2); // Add some randomness
        double sharpe = Math.max(-3.0, Math.min(3.0, baseRatio + variance)); // Cap between -3 and 3
        
        return BigDecimal.valueOf(sharpe).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMockRiskScore(int tradeCount, BigDecimal totalValue) {
        // Mock risk score calculation based on diversification and portfolio size
        double baseRisk = 50.0; // Base risk score
        
        // Lower risk with more diversification
        if (tradeCount > 5) {
            baseRisk -= 10.0;
        } else if (tradeCount > 10) {
            baseRisk -= 20.0;
        }
        
        // Adjust based on portfolio size
        if (totalValue.compareTo(BigDecimal.valueOf(1000000)) > 0) {
            baseRisk -= 5.0; // Lower risk for larger portfolios
        }
        
        // Add some randomness
        double variance = random.nextGaussian() * 5.0;
        double finalRisk = Math.max(0.0, Math.min(100.0, baseRisk + variance));
        
        return BigDecimal.valueOf(finalRisk).setScale(2, RoundingMode.HALF_UP);
    }
}