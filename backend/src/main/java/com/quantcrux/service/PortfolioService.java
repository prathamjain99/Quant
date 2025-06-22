package com.quantcrux.service;

import com.quantcrux.dto.PortfolioSummary;
import com.quantcrux.dto.TradeDTO;
import com.quantcrux.model.User;
import com.quantcrux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PortfolioService {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private UserRepository userRepository;

    public PortfolioSummary getPortfolioSummary(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Use TradeService to get trades with proper DTO projection
        List<TradeDTO> trades = tradeService.getUserTrades(username);

        // Calculate portfolio summary using DTOs
        double totalInvestment = trades.stream()
                .mapToDouble(trade -> trade.getNotional() * (trade.getEntryPrice() != null ? trade.getEntryPrice() / 100 : 1))
                .sum();

        double totalValue = trades.stream()
                .mapToDouble(trade -> {
                    double currentPrice = trade.getCurrentPrice() != null ? trade.getCurrentPrice() : trade.getEntryPrice();
                    return trade.getNotional() * (currentPrice != 0.0 ? currentPrice / 100 : 1);
                })
                .sum();

        double totalPnl = totalValue - totalInvestment;
        double pnlPercentage = totalInvestment > 0 ? (totalPnl / totalInvestment) * 100 : 0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalValue", totalValue);
        summary.put("totalInvestment", totalInvestment);
        summary.put("totalPnl", totalPnl);
        summary.put("pnlPercentage", pnlPercentage);
        summary.put("positionCount", trades.size());

        // Convert trades to position format using DTOs
        List<Map<String, Object>> positions = trades.stream().map(trade -> {
            Map<String, Object> position = new HashMap<>();
            position.put("id", trade.getId().toString());
            
            Map<String, Object> product = new HashMap<>();
            product.put("name", trade.getProductName());
            product.put("type", trade.getProductType());
            product.put("underlyingAsset", trade.getUnderlyingAsset());
            position.put("product", product);
            
            position.put("quantity", trade.getNotional());
            position.put("entryPrice", trade.getEntryPrice());
            position.put("currentValue", trade.getNotional() * (trade.getCurrentPrice() != null ? trade.getCurrentPrice() / 100 : trade.getEntryPrice() / 100));
            position.put("totalInvestment", trade.getNotional() * (trade.getEntryPrice() != null ? trade.getEntryPrice() / 100 : 1));
            position.put("unrealizedPnl", trade.getPnl());
            
            return position;
        }).collect(Collectors.toList());

        return new PortfolioSummary(summary, positions);
    }
}