package com.quantcrux.service;

import com.quantcrux.dto.ActivityLogResponse;
import com.quantcrux.model.User;
import com.quantcrux.model.UserActivityLog;
import com.quantcrux.repository.UserActivityLogRepository;
import com.quantcrux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserActivityService {

    @Autowired
    private UserActivityLogRepository activityLogRepository;

    @Autowired
    private UserRepository userRepository;

    public void logActivity(String username, String activityType, String description) {
        logActivity(username, activityType, description, null, null, null);
    }

    public void logActivity(String username, String activityType, String description, 
                           String entityType, Long entityId) {
        logActivity(username, activityType, description, entityType, entityId, null);
    }

    public void logActivity(String username, String activityType, String description, 
                           String entityType, Long entityId, HttpServletRequest request) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) return;

            UserActivityLog activity = new UserActivityLog(user, activityType, description, entityType, entityId);
            
            if (request != null) {
                activity.setIpAddress(getClientIpAddress(request));
                activity.setUserAgent(request.getHeader("User-Agent"));
            }

            activityLogRepository.save(activity);
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to log activity: " + e.getMessage());
        }
    }

    public List<ActivityLogResponse> getRecentActivities(String username, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(0, limit);
        List<UserActivityLog> activities = activityLogRepository
                .findByUserOrderByCreatedAtDesc(user, pageable)
                .getContent();

        return activities.stream()
                .map(ActivityLogResponse::new)
                .collect(Collectors.toList());
    }

    public List<ActivityLogResponse> getActivitiesSince(String username, LocalDateTime since) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserActivityLog> activities = activityLogRepository
                .findRecentActivities(user, since);

        return activities.stream()
                .map(ActivityLogResponse::new)
                .collect(Collectors.toList());
    }

    public Long getActivityCount(String username, String activityType) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return activityLogRepository.countByUserAndActivityType(user, activityType);
    }

    public String getMostUsedFeature(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Object[]> stats = activityLogRepository.getActivityStatsByUser(user);
        
        if (stats.isEmpty()) return "None";

        // Find the activity type with the highest count
        String mostUsed = "None";
        Long maxCount = 0L;
        
        for (Object[] stat : stats) {
            String activityType = (String) stat[0];
            Long count = (Long) stat[1];
            
            if (count > maxCount) {
                maxCount = count;
                mostUsed = formatActivityType(activityType);
            }
        }

        return mostUsed;
    }

    private String formatActivityType(String activityType) {
        if (activityType == null) return "Unknown";
        
        return switch (activityType) {
            case "STRATEGY_CREATED", "STRATEGY_UPDATED" -> "Strategy Builder";
            case "BACKTEST_STARTED", "BACKTEST_COMPLETED" -> "Backtesting";
            case "PRODUCT_CREATED" -> "Product Creator";
            case "TRADE_BOOKED" -> "Trading";
            case "REPORT_GENERATED" -> "Reports";
            default -> activityType.replace("_", " ").toLowerCase();
        };
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}