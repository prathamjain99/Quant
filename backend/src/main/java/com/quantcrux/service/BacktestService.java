package com.quantcrux.service;

import com.quantcrux.dto.BacktestRequest;
import com.quantcrux.dto.BacktestResult;
import com.quantcrux.dto.MarketDataPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BacktestService {

    @Autowired
    private MarketDataService marketDataService;

    public BacktestResult runBacktest(BacktestRequest request) {
        // Get market data for the specified period
        LocalDate startDate = LocalDate.parse(request.getStartDate());
        LocalDate endDate = LocalDate.parse(request.getEndDate());
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        
        List<MarketDataPoint> marketData = marketDataService.getMarketData(request.getSymbol(), (int) daysBetween);
        
        // Simple momentum strategy simulation
        double capital = request.getInitialCapital();
        double position = 0;
        double entryPrice = 0;
        List<Double> returns = new ArrayList<>();
        List<Map<String, Object>> equityCurve = new ArrayList<>();
        
        int totalTrades = 0;
        int profitableTrades = 0;
        double maxDrawdown = 0;
        double peakValue = capital;
        
        for (int i = 1; i < marketData.size(); i++) {
            MarketDataPoint current = marketData.get(i);
            MarketDataPoint previous = marketData.get(i - 1);
            
            double currentPrice = current.getClose();
            double previousPrice = previous.getClose();
            double dailyReturn = (currentPrice - previousPrice) / previousPrice;
            
            // Simple momentum strategy: buy if price is above 20-day average
            if (i >= 20) {
                double sma20 = marketData.subList(i - 20, i).stream()
                    .mapToDouble(MarketDataPoint::getClose)
                    .average()
                    .orElse(0);
                
                // Entry signal
                if (position == 0 && currentPrice > sma20 * 1.02) {
                    position = capital / currentPrice;
                    entryPrice = currentPrice;
                    totalTrades++;
                }
                // Exit signal
                else if (position > 0 && currentPrice < sma20 * 0.98) {
                    double exitValue = position * currentPrice;
                    if (exitValue > capital) {
                        profitableTrades++;
                    }
                    capital = exitValue;
                    position = 0;
                }
            }
            
            // Calculate current portfolio value
            double currentValue = position > 0 ? position * currentPrice : capital;
            
            // Track drawdown
            if (currentValue > peakValue) {
                peakValue = currentValue;
            } else {
                double drawdown = (peakValue - currentValue) / peakValue;
                maxDrawdown = Math.max(maxDrawdown, drawdown);
            }
            
            // Add to equity curve
            Map<String, Object> point = new HashMap<>();
            point.put("date", current.getDate().toString());
            point.put("value", currentValue);
            equityCurve.add(point);
            
            returns.add(dailyReturn);
        }
        
        // Final portfolio value
        double finalValue = position > 0 ? position * marketData.get(marketData.size() - 1).getClose() : capital;
        
        // Calculate metrics
        double totalReturn = (finalValue - request.getInitialCapital()) / request.getInitialCapital();
        double winRate = totalTrades > 0 ? (double) profitableTrades / totalTrades : 0;
        
        // Calculate Sharpe ratio (simplified)
        double avgReturn = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double stdDev = Math.sqrt(returns.stream()
            .mapToDouble(r -> Math.pow(r - avgReturn, 2))
            .average().orElse(0));
        double sharpeRatio = stdDev > 0 ? (avgReturn * 252) / (stdDev * Math.sqrt(252)) : 0;
        
        Map<String, Object> results = new HashMap<>();
        results.put("total_return", totalReturn);
        results.put("final_value", finalValue);
        results.put("total_trades", totalTrades);
        results.put("profitable_trades", profitableTrades);
        results.put("win_rate", winRate);
        results.put("max_drawdown", maxDrawdown);
        results.put("sharpe_ratio", Math.round(sharpeRatio * 100.0) / 100.0);
        
        return new BacktestResult(results, equityCurve);
    }
}