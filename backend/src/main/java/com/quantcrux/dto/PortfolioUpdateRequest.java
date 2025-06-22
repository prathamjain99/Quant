package com.quantcrux.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PortfolioUpdateRequest {
    @NotBlank(message = "Portfolio name is required")
    @Size(min = 2, max = 100, message = "Portfolio name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    // Constructors
    public PortfolioUpdateRequest() {}

    public PortfolioUpdateRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}