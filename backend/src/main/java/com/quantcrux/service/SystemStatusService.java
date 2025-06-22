package com.quantcrux.service;

import com.quantcrux.dto.SystemStatusResponse;
import com.quantcrux.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SystemStatusService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserSessionRepository sessionRepository;

    private final Random random = new Random();
    private final long startTime = System.currentTimeMillis();

    public SystemStatusResponse getSystemStatus() {
        SystemStatusResponse response = new SystemStatusResponse();

        // Check database status
        SystemStatusResponse.DatabaseStatus dbStatus = checkDatabaseStatus();
        response.setDatabase(dbStatus);

        // Check service statuses (mocked)
        response.setBacktestEngine(createMockServiceStatus("Backtest Engine", "1.0.0"));
        response.setAnalyticsEngine(createMockServiceStatus("Analytics Engine", "2.1.0"));
        response.setMarketDataService(createMockServiceStatus("Market Data Service", "1.5.0"));

        // Create system metrics
        SystemStatusResponse.SystemMetrics metrics = createSystemMetrics();
        response.setMetrics(metrics);

        // Determine overall status
        String overallStatus = determineOverallStatus(dbStatus, response.getBacktestEngine(), 
                                                     response.getAnalyticsEngine(), response.getMarketDataService());
        response.setOverallStatus(overallStatus);

        return response;
    }

    private SystemStatusResponse.DatabaseStatus checkDatabaseStatus() {
        SystemStatusResponse.DatabaseStatus status = new SystemStatusResponse.DatabaseStatus();
        
        try {
            long startTime = System.currentTimeMillis();
            
            try (Connection connection = dataSource.getConnection()) {
                // Test connection
                connection.isValid(5);
                
                long responseTime = System.currentTimeMillis() - startTime;
                status.setStatus("ONLINE");
                status.setConnectionPool("HikariCP");
                status.setActiveConnections(5 + random.nextInt(10)); // Mock values
                status.setMaxConnections(20);
                status.setResponseTimeMs(responseTime);
            }
        } catch (Exception e) {
            status.setStatus("OFFLINE");
            status.setResponseTimeMs(-1L);
        }

        return status;
    }

    private SystemStatusResponse.ServiceStatus createMockServiceStatus(String serviceName, String version) {
        SystemStatusResponse.ServiceStatus status = new SystemStatusResponse.ServiceStatus();
        
        // Mock service status - in real implementation, these would be actual health checks
        status.setStatus("ONLINE");
        status.setVersion(version);
        status.setUptimeMs(System.currentTimeMillis() - startTime);
        status.setResponseTimeMs(50L + random.nextInt(100)); // 50-150ms
        status.setLastError(null);

        return status;
    }

    private SystemStatusResponse.SystemMetrics createSystemMetrics() {
        SystemStatusResponse.SystemMetrics metrics = new SystemStatusResponse.SystemMetrics();
        
        // Mock system metrics - in real implementation, these would be actual system metrics
        metrics.setCpuUsage(20.0 + random.nextDouble() * 30.0); // 20-50%
        metrics.setMemoryUsage(40.0 + random.nextDouble() * 20.0); // 40-60%
        metrics.setDiskSpaceUsed(1024L * 1024L * 1024L * (50 + random.nextInt(30))); // 50-80GB
        metrics.setDiskSpaceTotal(1024L * 1024L * 1024L * 500L); // 500GB
        
        // Real metrics from database
        Long totalSessions = sessionRepository.count();
        Long activeSessions = sessionRepository.findAll().stream()
                .mapToLong(session -> session.getIsActive() ? 1 : 0)
                .sum();
        
        metrics.setActiveUsers(activeSessions.intValue());
        metrics.setTotalSessions(totalSessions.intValue());

        return metrics;
    }

    private String determineOverallStatus(SystemStatusResponse.DatabaseStatus dbStatus, 
                                        SystemStatusResponse.ServiceStatus... services) {
        if (!"ONLINE".equals(dbStatus.getStatus())) {
            return "ERROR";
        }

        for (SystemStatusResponse.ServiceStatus service : services) {
            if (!"ONLINE".equals(service.getStatus())) {
                return "WARNING";
            }
        }

        return "HEALTHY";
    }
}