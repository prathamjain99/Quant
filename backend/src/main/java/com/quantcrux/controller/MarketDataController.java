package com.quantcrux.controller;

import com.quantcrux.dto.MarketDataPoint;
import com.quantcrux.service.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/market-data")
public class MarketDataController {

    @Autowired
    private MarketDataService marketDataService;

    @GetMapping("/{symbol}")
    public ResponseEntity<List<MarketDataPoint>> getMarketData(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "252") int days) {
        
        List<MarketDataPoint> data = marketDataService.getMarketData(symbol, days);
        return ResponseEntity.ok(data);
    }
}