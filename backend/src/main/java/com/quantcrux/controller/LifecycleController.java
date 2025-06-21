package com.quantcrux.controller;

import com.quantcrux.service.LifecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/lifecycle")
public class LifecycleController {

    @Autowired
    private LifecycleService lifecycleService;

    @GetMapping("/events/{tradeId}")
    public ResponseEntity<List<Map<String, Object>>> getTradeEvents(@PathVariable Long tradeId) {
        List<Map<String, Object>> events = lifecycleService.getTradeEvents(tradeId);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/process-fixings")
    public ResponseEntity<?> processFixings() {
        lifecycleService.processFixings();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Fixings processed successfully");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-barriers")
    public ResponseEntity<?> checkBarriers() {
        lifecycleService.checkBarriers();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Barriers checked successfully");
        
        return ResponseEntity.ok(response);
    }
}