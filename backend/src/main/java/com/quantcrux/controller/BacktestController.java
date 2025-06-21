package com.quantcrux.controller;

import com.quantcrux.dto.BacktestRequest;
import com.quantcrux.dto.BacktestResult;
import com.quantcrux.service.BacktestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/backtest")
public class BacktestController {

    @Autowired
    private BacktestService backtestService;

    @PostMapping
    public ResponseEntity<BacktestResult> runBacktest(@Valid @RequestBody BacktestRequest request) {
        BacktestResult result = backtestService.runBacktest(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getBacktestHistory() {
        // Return empty list for now
        return ResponseEntity.ok(java.util.Collections.emptyList());
    }
}