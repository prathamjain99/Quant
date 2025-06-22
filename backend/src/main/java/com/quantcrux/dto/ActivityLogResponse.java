package com.quantcrux.dto;

import com.quantcrux.model.UserActivityLog;

import java.time.LocalDateTime;

public class ActivityLogResponse {
    private Long id;
    private String activityType;
    private String description;
    private String entityType;
    private Long entityId;
    private LocalDateTime createdAt;
    private String timeAgo;

    // Constructors
    public ActivityLogResponse() {}

    public ActivityLogResponse(UserActivityLog activity) {
        this.id = activity.getId();
        this.activityType = activity.getActivityType();
        this.description = activity.getDescription();
        this.entityType = activity.getEntityType();
        this.entityId = activity.getEntityId();
        this.createdAt = activity.getCreatedAt();
        this.timeAgo = calculateTimeAgo(activity.getCreatedAt());
    }

    private String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null) return "Unknown";
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();
        
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minutes ago";
        
        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";
        
        long days = hours / 24;
        if (days < 7) return days + " days ago";
        
        long weeks = days / 7;
        if (weeks < 4) return weeks + " weeks ago";
        
        long months = days / 30;
        return months + " months ago";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getTimeAgo() { return timeAgo; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }
}