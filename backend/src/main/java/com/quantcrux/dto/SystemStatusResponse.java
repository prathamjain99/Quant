package com.quantcrux.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class SystemStatusResponse {
    private String overallStatus; // "HEALTHY", "WARNING", "ERROR"
    private DatabaseStatus database;
    private ServiceStatus backtestEngine;
    private ServiceStatus analyticsEngine;
    private ServiceStatus marketDataService;
    private SystemMetrics metrics;
    private LocalDateTime timestamp;

    // Constructors
    public SystemStatusResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }

    public DatabaseStatus getDatabase() { return database; }
    public void setDatabase(DatabaseStatus database) { this.database = database; }

    public ServiceStatus getBacktestEngine() { return backtestEngine; }
    public void setBacktestEngine(ServiceStatus backtestEngine) { this.backtestEngine = backtestEngine; }

    public ServiceStatus getAnalyticsEngine() { return analyticsEngine; }
    public void setAnalyticsEngine(ServiceStatus analyticsEngine) { this.analyticsEngine = analyticsEngine; }

    public ServiceStatus getMarketDataService() { return marketDataService; }
    public void setMarketDataService(ServiceStatus marketDataService) { this.marketDataService = marketDataService; }

    public SystemMetrics getMetrics() { return metrics; }
    public void setMetrics(SystemMetrics metrics) { this.metrics = metrics; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // Inner classes
    public static class DatabaseStatus {
        private String status;
        private String connectionPool;
        private Integer activeConnections;
        private Integer maxConnections;
        private Long responseTimeMs;

        // Constructors, getters and setters
        public DatabaseStatus() {}

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getConnectionPool() { return connectionPool; }
        public void setConnectionPool(String connectionPool) { this.connectionPool = connectionPool; }

        public Integer getActiveConnections() { return activeConnections; }
        public void setActiveConnections(Integer activeConnections) { this.activeConnections = activeConnections; }

        public Integer getMaxConnections() { return maxConnections; }
        public void setMaxConnections(Integer maxConnections) { this.maxConnections = maxConnections; }

        public Long getResponseTimeMs() { return responseTimeMs; }
        public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    }

    public static class ServiceStatus {
        private String status;
        private String version;
        private Long uptimeMs;
        private Long responseTimeMs;
        private String lastError;

        // Constructors, getters and setters
        public ServiceStatus() {}

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public Long getUptimeMs() { return uptimeMs; }
        public void setUptimeMs(Long uptimeMs) { this.uptimeMs = uptimeMs; }

        public Long getResponseTimeMs() { return responseTimeMs; }
        public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

        public String getLastError() { return lastError; }
        public void setLastError(String lastError) { this.lastError = lastError; }
    }

    public static class SystemMetrics {
        private Double cpuUsage;
        private Double memoryUsage;
        private Long diskSpaceUsed;
        private Long diskSpaceTotal;
        private Integer activeUsers;
        private Integer totalSessions;

        // Constructors, getters and setters
        public SystemMetrics() {}

        public Double getCpuUsage() { return cpuUsage; }
        public void setCpuUsage(Double cpuUsage) { this.cpuUsage = cpuUsage; }

        public Double getMemoryUsage() { return memoryUsage; }
        public void setMemoryUsage(Double memoryUsage) { this.memoryUsage = memoryUsage; }

        public Long getDiskSpaceUsed() { return diskSpaceUsed; }
        public void setDiskSpaceUsed(Long diskSpaceUsed) { this.diskSpaceUsed = diskSpaceUsed; }

        public Long getDiskSpaceTotal() { return diskSpaceTotal; }
        public void setDiskSpaceTotal(Long diskSpaceTotal) { this.diskSpaceTotal = diskSpaceTotal; }

        public Integer getActiveUsers() { return activeUsers; }
        public void setActiveUsers(Integer activeUsers) { this.activeUsers = activeUsers; }

        public Integer getTotalSessions() { return totalSessions; }
        public void setTotalSessions(Integer totalSessions) { this.totalSessions = totalSessions; }
    }
}