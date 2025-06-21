package com.quantcrux.controller;

import com.quantcrux.dto.TradeRequest;
import com.quantcrux.model.Product;
import com.quantcrux.model.Trade;
import com.quantcrux.model.User;
import com.quantcrux.repository.ProductRepository;
import com.quantcrux.repository.TradeRepository;
import com.quantcrux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/trades")
public class TradeController {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/book")
    public ResponseEntity<?> bookTrade(@Valid @RequestBody TradeRequest request, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Trade trade = new Trade();
        trade.setProduct(product);
        trade.setTradeType(request.getTradeType());
        trade.setNotional(request.getNotional());
        trade.setEntryPrice(request.getEntryPrice());
        trade.setCurrentPrice(request.getEntryPrice()); // Initially same as entry price
        trade.setNotes(request.getNotes());
        trade.setUser(user);

        Trade savedTrade = tradeRepository.save(trade);
        
        Map<String, Object> response = new HashMap<>();
        response.put("tradeId", savedTrade.getId());
        response.put("status", "BOOKED");
        response.put("message", "Trade booked successfully");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getTrades(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Trade> trades = tradeRepository.findByUser(user);
        
        List<Map<String, Object>> tradeResponses = trades.stream().map(trade -> {
            Map<String, Object> tradeMap = new HashMap<>();
            tradeMap.put("id", trade.getId());
            tradeMap.put("productName", trade.getProduct().getName());
            tradeMap.put("tradeType", trade.getTradeType());
            tradeMap.put("status", trade.getStatus().name());
            tradeMap.put("notional", trade.getNotional());
            tradeMap.put("entryPrice", trade.getEntryPrice());
            tradeMap.put("currentPrice", trade.getCurrentPrice());
            
            // Calculate P&L
            double pnl = 0.0;
            if (trade.getCurrentPrice() != null && trade.getEntryPrice() != null) {
                if ("BUY".equals(trade.getTradeType())) {
                    pnl = (trade.getCurrentPrice() - trade.getEntryPrice()) * trade.getNotional() / 100;
                } else {
                    pnl = (trade.getEntryPrice() - trade.getCurrentPrice()) * trade.getNotional() / 100;
                }
            }
            tradeMap.put("pnl", pnl);
            tradeMap.put("tradeDate", trade.getTradeDate());
            
            return tradeMap;
        }).toList();

        return ResponseEntity.ok(tradeResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trade> getTrade(@PathVariable Long id, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found"));

        if (!trade.getUser().getId().equals(user.getId())) {
            return ResponseEntity.forbidden().build();
        }

        return ResponseEntity.ok(trade);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTradeStatus(@PathVariable Long id, @RequestParam String status, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found"));

        if (!trade.getUser().getId().equals(user.getId())) {
            return ResponseEntity.forbidden().build();
        }

        try {
            trade.setStatus(Trade.TradeStatus.valueOf(status.toUpperCase()));
            tradeRepository.save(trade);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Trade status updated successfully");
            response.put("status", status);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status: " + status));
        }
    }
}