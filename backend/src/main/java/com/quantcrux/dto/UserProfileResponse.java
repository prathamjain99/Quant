package com.quantcrux.dto;

import com.quantcrux.model.User;

import java.time.LocalDateTime;

public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String role;
    private String roleDescription;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private String phoneNumber;
    private String department;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private Integer activeSessions;

    public UserProfileResponse() {}

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole().name().toLowerCase();
        this.roleDescription = user.getRole().getDescription();
        this.isActive = user.getIsActive();
        this.isEmailVerified = user.getIsEmailVerified();
        this.phoneNumber = user.getPhoneNumber();
        this.department = user.getDepartment();
        this.lastLogin = user.getLastLogin();
        this.createdAt = user.getCreatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRoleDescription() { return roleDescription; }
    public void setRoleDescription(String roleDescription) { this.roleDescription = roleDescription; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsEmailVerified() { return isEmailVerified; }
    public void setIsEmailVerified(Boolean isEmailVerified) { this.isEmailVerified = isEmailVerified; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getActiveSessions() { return activeSessions; }
    public void setActiveSessions(Integer activeSessions) { this.activeSessions = activeSessions; }
}