package com.quantcrux.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_log")
public class UserActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_type", nullable = false)
    private String activityType;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    // Constructors
    public UserActivityLog() {}

    public UserActivityLog(User user, String activityType, String description) {
        this.user = user;
        this.activityType = activityType;
        this.description = description;
    }

    public UserActivityLog(User user, String activityType, String description, String entityType, Long entityId) {
        this.user = user;
        this.activityType = activityType;
        this.description = description;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    // Activity Types Constants
    public static class ActivityType {
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String STRATEGY_CREATED = "STRATEGY_CREATED";
        public static final String STRATEGY_UPDATED = "STRATEGY_UPDATED";
        public static final String STRATEGY_DELETED = "STRATEGY_DELETED";
        public static final String BACKTEST_STARTED = "BACKTEST_STARTED";
        public static final String BACKTEST_COMPLETED = "BACKTEST_COMPLETED";
        public static final String PRODUCT_CREATED = "PRODUCT_CREATED";
        public static final String TRADE_BOOKED = "TRADE_BOOKED";
        public static final String REPORT_GENERATED = "REPORT_GENERATED";
        public static final String PROFILE_UPDATED = "PROFILE_UPDATED";
        public static final String PASSWORD_CHANGED = "PASSWORD_CHANGED";
    }
}