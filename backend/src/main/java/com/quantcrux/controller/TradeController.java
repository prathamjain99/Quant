package com.quantcrux.controller;

import com.quantcrux.dto.TradeDTO;
import com.quantcrux.dto.TradeRequest;
import com.quantcrux.model.Product;
import com.quantcrux.model.Trade;
import com.quantcrux.model.User;
import com.quantcrux.repository.ProductRepository;
import com.quantcrux.repository.TradeRepository;
import com.quantcrux.repository.UserRepository;
import com.quantcrux.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// @CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, maxAge = 3600)
@RestController
@RequestMapping("/api/trades")
public class TradeController {

    @Autowired
    private TradeService tradeService;

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

    /**
     * Get all trades for the authenticated user using DTO projection
     */
    @GetMapping
    public ResponseEntity<List<TradeDTO>> getTrades(Authentication authentication) {
        List<TradeDTO> trades = tradeService.getUserTrades(authentication.getName());
        return ResponseEntity.ok(trades);
    }

    /**
     * Get a specific trade by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TradeDTO> getTrade(@PathVariable Long id, Authentication authentication) {
        TradeDTO trade = tradeService.getUserTrade(authentication.getName(), id);
        return ResponseEntity.ok(trade);
    }

    /**
     * Update trade status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTradeStatus(@PathVariable Long id, @RequestParam String status, Authentication authentication) {
        try {
            Trade.TradeStatus tradeStatus = Trade.TradeStatus.valueOf(status.toUpperCase());
            tradeService.updateTradeStatus(authentication.getName(), id, tradeStatus);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Trade status updated successfully");
            response.put("status", status);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status: " + status));
        }
    }

    /**
     * Alternative endpoint using JPQL projection
     */
    @GetMapping("/projection")
    public ResponseEntity<List<TradeDTO>> getTradesWithProjection(Authentication authentication) {
        List<TradeDTO> trades = tradeService.getUserTradesWithProjection(authentication.getName());
        return ResponseEntity.ok(trades);
    }
}