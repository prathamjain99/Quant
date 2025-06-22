package com.quantcrux.service;

import com.quantcrux.dto.MarketSnapshotResponse;
import com.quantcrux.model.MarketData;
import com.quantcrux.repository.MarketDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarketDataUpdateService {

    @Autowired
    private MarketDataRepository marketDataRepository;

    private final Random random = new Random();

    public List<MarketSnapshotResponse> getMarketSnapshot() {
        List<String> symbols = Arrays.asList("NIFTY", "SENSEX", "BTC", "ETH", "SPY", "QQQ");
        
        List<MarketData> marketDataList = marketDataRepository.findBySymbolsAndIsActiveTrue(symbols);
        
        // If no data exists, create initial data
        if (marketDataList.isEmpty()) {
            initializeMarketData();
            marketDataList = marketDataRepository.findBySymbolsAndIsActiveTrue(symbols);
        } else {
            // Update existing data with new prices
            updateMarketPrices(marketDataList);
        }

        return marketDataList.stream()
                .map(MarketSnapshotResponse::new)
                .collect(Collectors.toList());
    }

    private void initializeMarketData() {
        createMarketData("NIFTY", "NIFTY 50", new BigDecimal("19500.00"), "INR", "NSE", "Index");
        createMarketData("SENSEX", "BSE SENSEX", new BigDecimal("65000.00"), "INR", "BSE", "Index");
        createMarketData("BTC", "Bitcoin", new BigDecimal("42000.00"), "USD", "CRYPTO", "Cryptocurrency");
        createMarketData("ETH", "Ethereum", new BigDecimal("2500.00"), "USD", "CRYPTO", "Cryptocurrency");
        createMarketData("SPY", "SPDR S&P 500 ETF", new BigDecimal("450.00"), "USD", "NYSE", "ETF");
        createMarketData("QQQ", "Invesco QQQ Trust", new BigDecimal("380.00"), "USD", "NASDAQ", "ETF");
    }

    private void createMarketData(String symbol, String name, BigDecimal basePrice, 
                                 String currency, String exchange, String sector) {
        MarketData marketData = new MarketData();
        marketData.setSymbol(symbol);
        marketData.setName(name);
        marketData.setPrice(basePrice);
        marketData.setPreviousClose(basePrice);
        marketData.setChangeAmount(BigDecimal.ZERO);
        marketData.setChangePercent(BigDecimal.ZERO);
        marketData.setVolume(1000000L + random.nextInt(5000000));
        marketData.setCurrency(currency);
        marketData.setExchange(exchange);
        marketData.setSector(sector);
        marketData.setUpdatedAt(LocalDateTime.now());
        
        marketDataRepository.save(marketData);
    }

    private void updateMarketPrices(List<MarketData> marketDataList) {
        for (MarketData marketData : marketDataList) {
            // Generate realistic price movement (±2% max change)
            double changePercent = (random.nextGaussian() * 0.5); // Normal distribution, ±1.5% typical
            changePercent = Math.max(-2.0, Math.min(2.0, changePercent)); // Cap at ±2%
            
            BigDecimal currentPrice = marketData.getPrice();
            BigDecimal changeAmount = currentPrice.multiply(BigDecimal.valueOf(changePercent / 100.0));
            BigDecimal newPrice = currentPrice.add(changeAmount);
            
            // Ensure price doesn't go negative
            if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
                newPrice = currentPrice.multiply(BigDecimal.valueOf(0.99));
                changeAmount = newPrice.subtract(currentPrice);
                changePercent = changeAmount.divide(currentPrice, 4, RoundingMode.HALF_UP)
                              .multiply(BigDecimal.valueOf(100)).doubleValue();
            }

            marketData.setPreviousClose(currentPrice);
            marketData.setPrice(newPrice.setScale(2, RoundingMode.HALF_UP));
            marketData.setChangeAmount(changeAmount.setScale(2, RoundingMode.HALF_UP));
            marketData.setChangePercent(BigDecimal.valueOf(changePercent).setScale(2, RoundingMode.HALF_UP));
            marketData.setVolume(1000000L + random.nextInt(5000000));
            marketData.setUpdatedAt(LocalDateTime.now());
            
            marketDataRepository.save(marketData);
        }
    }

    public void simulateMarketUpdate() {
        List<MarketData> allMarketData = marketDataRepository.findByIsActiveTrueOrderBySymbol();
        if (!allMarketData.isEmpty()) {
            updateMarketPrices(allMarketData);
        }
    }
}