package com.quantcrux.controller;

import com.quantcrux.dto.ActivityLogResponse;
import com.quantcrux.dto.MarketSnapshotResponse;
import com.quantcrux.dto.SystemStatusResponse;
import com.quantcrux.dto.UserSummaryResponse;
import com.quantcrux.service.DashboardService;
import com.quantcrux.service.MarketDataUpdateService;
import com.quantcrux.service.SystemStatusService;
import com.quantcrux.service.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, maxAge = 3600)
@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private MarketDataUpdateService marketDataUpdateService;

    @Autowired
    private SystemStatusService systemStatusService;

    @GetMapping("/user/summary")
    public ResponseEntity<UserSummaryResponse> getUserSummary(Authentication authentication) {
        try {
            UserSummaryResponse summary = dashboardService.getUserSummary(authentication.getName());
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/activity")
    public ResponseEntity<List<ActivityLogResponse>> getUserActivity(
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ActivityLogResponse> activities = userActivityService.getRecentActivities(
                    authentication.getName(), limit);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/market/snapshot")
    public ResponseEntity<List<MarketSnapshotResponse>> getMarketSnapshot() {
        try {
            List<MarketSnapshotResponse> snapshot = marketDataUpdateService.getMarketSnapshot();
            return ResponseEntity.ok(snapshot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/system/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemStatusResponse> getSystemStatus() {
        try {
            SystemStatusResponse status = systemStatusService.getSystemStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/market/simulate-update")
    public ResponseEntity<Map<String, String>> simulateMarketUpdate() {
        try {
            marketDataUpdateService.simulateMarketUpdate();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Market data updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update market data");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}