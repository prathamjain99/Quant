package com.quantcrux.controller;

import com.quantcrux.dto.PricingRequest;
import com.quantcrux.dto.PricingResult;
import com.quantcrux.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

// @CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, maxAge = 3600)
@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    @Autowired
    private PricingService pricingService;

    @PostMapping("/calculate")
    public ResponseEntity<PricingResult> calculatePrice(@Valid @RequestBody PricingRequest request) {
        PricingResult result = pricingService.calculatePrice(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/monte-carlo")
    public ResponseEntity<PricingResult> monteCarloPrice(@Valid @RequestBody PricingRequest request) {
        PricingResult result = pricingService.monteCarloPrice(request);
        return ResponseEntity.ok(result);
    }
}