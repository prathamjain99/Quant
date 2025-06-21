package com.quantcrux.service;

import com.quantcrux.dto.MarketDataPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class MarketDataService {

    public List<MarketDataPoint> getMarketData(String symbol, int days) {
        List<MarketDataPoint> data = new ArrayList<>();
        Random random = new Random(42); // Fixed seed for consistent data
        
        double basePrice = getBasePrice(symbol);
        double currentPrice = basePrice;
        
        for (int i = 0; i < days; i++) {
            LocalDate date = LocalDate.now().minusDays(days - i - 1);
            
            // Generate realistic price movements
            double dailyReturn = (random.nextGaussian() * 0.02) + 0.0002; // 2% daily volatility, slight upward drift
            double open = currentPrice;
            double high = open * (1 + Math.abs(random.nextGaussian() * 0.01));
            double low = open * (1 - Math.abs(random.nextGaussian() * 0.01));
            double close = open * (1 + dailyReturn);
            
            // Ensure high >= max(open, close) and low <= min(open, close)
            high = Math.max(high, Math.max(open, close));
            low = Math.min(low, Math.min(open, close));
            
            long volume = (long) (1000000 + random.nextInt(5000000));
            
            data.add(new MarketDataPoint(date, open, high, low, close, volume));
            currentPrice = close;
        }
        
        return data;
    }
    
    private double getBasePrice(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "SPY" -> 400.0;
            case "AAPL" -> 150.0;
            case "MSFT" -> 300.0;
            case "GOOGL" -> 2500.0;
            case "TSLA" -> 200.0;
            case "EUR/USD" -> 1.10;
            case "GBP/USD" -> 1.25;
            case "USD/JPY" -> 110.0;
            default -> 100.0;
        };
    }
}