package com.quantcrux.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public class StrategyV2UpdateRequest {
    @NotBlank(message = "Strategy name is required")
    @Size(min = 2, max = 100, message = "Strategy name must be between 2 and 100 characters")
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private Map<String, Object> codeJson;
    private List<String> tags;

    // Constructors
    public StrategyV2UpdateRequest() {}

    public StrategyV2UpdateRequest(String name, String description, Map<String, Object> codeJson, List<String> tags) {
        this.name = name;
        this.description = description;
        this.codeJson = codeJson;
        this.tags = tags;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Object> getCodeJson() { return codeJson; }
    public void setCodeJson(Map<String, Object> codeJson) { this.codeJson = codeJson; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}