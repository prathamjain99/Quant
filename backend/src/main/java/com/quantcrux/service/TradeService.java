package com.quantcrux.service;

import com.quantcrux.dto.TradeDTO;
import com.quantcrux.model.Trade;
import com.quantcrux.model.User;
import com.quantcrux.repository.TradeRepository;
import com.quantcrux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all trades for a user with proper DTO projection to avoid lazy loading issues
     */
    public List<TradeDTO> getUserTrades(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch trades with eager loading of related entities
        List<Trade> trades = tradeRepository.findByUserWithProductAndUser(user);
        
        // Convert to DTOs within the active session
        return trades.stream()
                .map(TradeDTO::fromTrade)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific trade by ID for a user
     */
    public TradeDTO getUserTrade(String username, Long tradeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trade trade = tradeRepository.findByIdAndUserWithProduct(tradeId, user)
                .orElseThrow(() -> new RuntimeException("Trade not found"));

        return TradeDTO.fromTrade(trade);
    }

    /**
     * Get all trades using JPQL projection (alternative approach)
     */
    public List<TradeDTO> getUserTradesWithProjection(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return tradeRepository.findTradeProjectionsByUser(user);
    }

    /**
     * Update trade status
     */
    @Transactional
    public void updateTradeStatus(String username, Long tradeId, Trade.TradeStatus status) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trade trade = tradeRepository.findByIdAndUser(tradeId, user)
                .orElseThrow(() -> new RuntimeException("Trade not found"));

        trade.setStatus(status);
        tradeRepository.save(trade);
    }
}