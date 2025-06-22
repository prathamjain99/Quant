package com.quantcrux.dto;

import com.quantcrux.model.StrategyV2;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StrategyV2DTO {
    private Long id;
    private String name;
    private String description;
    private Map<String, Object> codeJson;
    private List<String> tags;
    private Boolean isPublic;
    private Long ownerId;
    private String ownerName;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    
    // Additional metadata
    private Boolean canEdit;
    private Boolean canDelete;
    private Boolean canPublish;

    // Default constructor
    public StrategyV2DTO() {}

    // Static factory method from StrategyV2 entity
    public static StrategyV2DTO fromStrategy(StrategyV2 strategy) {
        StrategyV2DTO dto = new StrategyV2DTO();
        dto.setId(strategy.getId());
        dto.setName(strategy.getName());
        dto.setDescription(strategy.getDescription());
        dto.setCodeJson(strategy.getCodeJson());
        dto.setTags(strategy.getTags());
        dto.setIsPublic(strategy.getIsPublic());
        dto.setCreatedAt(strategy.getCreatedAt());
        dto.setUpdatedAt(strategy.getUpdatedAt());
        dto.setPublishedAt(strategy.getPublishedAt());
        
        if (strategy.getOwner() != null) {
            dto.setOwnerId(strategy.getOwner().getId());
            dto.setOwnerName(strategy.getOwner().getName());
            dto.setOwnerUsername(strategy.getOwner().getUsername());
        }
        
        return dto;
    }

    // Static factory method with permissions
    public static StrategyV2DTO fromStrategyWithPermissions(StrategyV2 strategy, com.quantcrux.model.User currentUser) {
        StrategyV2DTO dto = fromStrategy(strategy);
        
        if (currentUser != null) {
            dto.setCanEdit(strategy.canBeModifiedBy(currentUser));
            dto.setCanDelete(strategy.canBeModifiedBy(currentUser));
            dto.setCanPublish(strategy.canBeModifiedBy(currentUser) && !strategy.getIsPublic());
        }
        
        return dto;
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

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public Boolean getCanEdit() { return canEdit; }
    public void setCanEdit(Boolean canEdit) { this.canEdit = canEdit; }

    public Boolean getCanDelete() { return canDelete; }
    public void setCanDelete(Boolean canDelete) { this.canDelete = canDelete; }

    public Boolean getCanPublish() { return canPublish; }
    public void setCanPublish(Boolean canPublish) { this.canPublish = canPublish; }
}