package com.quantcrux.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "strategies_v2")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StrategyV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Strategy name is required")
    @Size(min = 2, max = 100, message = "Strategy name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "code_json", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> codeJson;

    @ElementCollection
    @CollectionTable(name = "strategy_v2_tags", joinColumns = @JoinColumn(name = "strategy_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public StrategyV2() {}

    public StrategyV2(String name, String description, Map<String, Object> codeJson, User owner) {
        this.name = name;
        this.description = description;
        this.codeJson = codeJson;
        this.owner = owner;
    }

    // Helper methods
    public void publish() {
        this.isPublic = true;
        this.publishedAt = LocalDateTime.now();
    }

    public void unpublish() {
        this.isPublic = false;
        this.publishedAt = null;
    }

    public boolean isOwnedBy(User user) {
        return this.owner != null && this.owner.getId().equals(user.getId());
    }

    public boolean canBeViewedBy(User user) {
        if (user == null) return false;
        
        // Owner can always view
        if (isOwnedBy(user)) return true;
        
        // Portfolio managers can view all strategies
        if (User.Role.PORTFOLIO_MANAGER.equals(user.getRole())) return true;
        
        // Clients can only view public strategies
        if (User.Role.CLIENT.equals(user.getRole())) return this.isPublic;
        
        return false;
    }

    public boolean canBeModifiedBy(User user) {
        return user != null && 
               User.Role.RESEARCHER.equals(user.getRole()) && 
               isOwnedBy(user);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Object> getCodeJson() { return codeJson; }
    public void setCodeJson(Map<String, Object> codeJson) { this.codeJson = codeJson; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}