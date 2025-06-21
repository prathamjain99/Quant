package com.quantcrux.service;

import com.quantcrux.dto.PricingRequest;
import com.quantcrux.dto.PricingResult;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PricingService {

    private final Random random = new Random(42); // Fixed seed for consistent results
    private final NormalDistribution normalDist = new NormalDistribution();

    public PricingResult calculatePrice(PricingRequest request) {
        return monteCarloPrice(request);
    }

    public PricingResult monteCarloPrice(PricingRequest request) {
        int numSimulations = request.getNumSimulations();
        double[] payoffs = new double[numSimulations];
        
        for (int i = 0; i < numSimulations; i++) {
            double finalPrice = simulatePrice(request.getSpotPrice(), request.getVolatility(), 
                                            request.getRiskFreeRate(), request.getTimeToMaturity());
            payoffs[i] = calculatePayoff(finalPrice, request);
        }
        
        // Calculate statistics
        double meanPayoff = java.util.Arrays.stream(payoffs).average().orElse(0);
        double variance = java.util.Arrays.stream(payoffs)
            .map(p -> Math.pow(p - meanPayoff, 2))
            .average().orElse(0);
        double stdDev = Math.sqrt(variance);
        
        // Discount to present value
        double price = meanPayoff * Math.exp(-request.getRiskFreeRate() * request.getTimeToMaturity());
        
        // Calculate Greeks (simplified finite difference)
        Map<String, Double> greeks = calculateGreeks(request);
        
        // 95% confidence interval
        double confidenceInterval = 1.96 * stdDev / Math.sqrt(numSimulations);
        
        return new PricingResult(price, greeks, confidenceInterval, numSimulations);
    }
    
    private double simulatePrice(double spot, double volatility, double riskFreeRate, double timeToMaturity) {
        double drift = riskFreeRate - 0.5 * volatility * volatility;
        double randomShock = normalDist.sample();
        return spot * Math.exp(drift * timeToMaturity + volatility * Math.sqrt(timeToMaturity) * randomShock);
    }
    
    private double calculatePayoff(double finalPrice, PricingRequest request) {
        return switch (request.getProductType().toLowerCase()) {
            case "digital_option" -> finalPrice > request.getStrike() ? request.getCoupon() * 100 : 0;
            case "barrier_option" -> {
                if (request.getBarrier() != null) {
                    yield finalPrice > request.getBarrier() && finalPrice > request.getStrike() ? 
                        request.getCoupon() * 100 : 0;
                } else {
                    yield finalPrice > request.getStrike() ? request.getCoupon() * 100 : 0;
                }
            }
            default -> Math.max(finalPrice - request.getStrike(), 0);
        };
    }
    
    private Map<String, Double> calculateGreeks(PricingRequest request) {
        Map<String, Double> greeks = new HashMap<>();
        
        // Simplified Greeks calculation using finite differences
        double epsilon = 0.01;
        
        // Delta: sensitivity to underlying price
        PricingRequest upRequest = copyRequest(request);
        upRequest.setSpotPrice(request.getSpotPrice() * (1 + epsilon));
        double priceUp = monteCarloPrice(upRequest).getPrice();
        
        PricingRequest downRequest = copyRequest(request);
        downRequest.setSpotPrice(request.getSpotPrice() * (1 - epsilon));
        double priceDown = monteCarloPrice(downRequest).getPrice();
        
        double delta = (priceUp - priceDown) / (2 * request.getSpotPrice() * epsilon);
        greeks.put("delta", Math.round(delta * 10000.0) / 10000.0);
        
        // Gamma: second derivative with respect to spot
        double basePrice = calculatePrice(request).getPrice();
        double gamma = (priceUp - 2 * basePrice + priceDown) / Math.pow(request.getSpotPrice() * epsilon, 2);
        greeks.put("gamma", Math.round(gamma * 10000.0) / 10000.0);
        
        // Vega: sensitivity to volatility
        PricingRequest vegaRequest = copyRequest(request);
        vegaRequest.setVolatility(request.getVolatility() + 0.01);
        double vegaPrice = calculatePrice(vegaRequest).getPrice();
        double vega = vegaPrice - basePrice;
        greeks.put("vega", Math.round(vega * 10000.0) / 10000.0);
        
        // Theta: time decay
        PricingRequest thetaRequest = copyRequest(request);
        thetaRequest.setTimeToMaturity(request.getTimeToMaturity() - 1.0/365.0);
        double thetaPrice = calculatePrice(thetaRequest).getPrice();
        double theta = thetaPrice - basePrice;
        greeks.put("theta", Math.round(theta * 10000.0) / 10000.0);
        
        return greeks;
    }
    
    private PricingRequest copyRequest(PricingRequest original) {
        PricingRequest copy = new PricingRequest();
        copy.setProductType(original.getProductType());
        copy.setSpotPrice(original.getSpotPrice());
        copy.setStrike(original.getStrike());
        copy.setBarrier(original.getBarrier());
        copy.setCoupon(original.getCoupon());
        copy.setVolatility(original.getVolatility());
        copy.setRiskFreeRate(original.getRiskFreeRate());
        copy.setTimeToMaturity(original.getTimeToMaturity());
        copy.setNumSimulations(10000); // Use fewer simulations for Greeks calculation
        return copy;
    }
}