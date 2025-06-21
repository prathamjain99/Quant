package com.quantcrux.config;

import com.quantcrux.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private SessionService sessionService;

    // Clean up expired sessions every hour
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupExpiredSessions() {
        try {
            sessionService.cleanupExpiredSessions();
            System.out.println("Cleaned up expired sessions at: " + new java.util.Date());
        } catch (Exception e) {
            System.err.println("Error cleaning up expired sessions: " + e.getMessage());
        }
    }
}