package com.quantcrux.service;

import com.quantcrux.dto.UserProfileResponse;
import com.quantcrux.dto.UserRegistrationRequest;
import com.quantcrux.model.User;
import com.quantcrux.model.UserSession;
import com.quantcrux.repository.UserRepository;
import com.quantcrux.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository sessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(UserRegistrationRequest request, String createdBy) {
        // Validate password confirmation
        if (!request.isPasswordMatching()) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDepartment(request.getDepartment());
        user.setCreatedBy(createdBy);
        user.setIsActive(true);
        user.setIsEmailVerified(false);

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserProfileResponse profile = new UserProfileResponse(user);
        
        // Get active sessions count
        Long activeSessionsCount = sessionRepository.countByUserAndIsActiveTrue(user);
        profile.setActiveSessions(activeSessionsCount.intValue());
        
        return profile;
    }

    public User updateUserProfile(String username, UserProfileResponse profileUpdate) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update allowed fields
        if (profileUpdate.getName() != null) {
            user.setName(profileUpdate.getName());
        }
        if (profileUpdate.getPhoneNumber() != null) {
            user.setPhoneNumber(profileUpdate.getPhoneNumber());
        }
        if (profileUpdate.getDepartment() != null) {
            user.setDepartment(profileUpdate.getDepartment());
        }

        return userRepository.save(user);
    }

    public void updateLastLogin(Long userId) {
        userRepository.updateLastLogin(userId, LocalDateTime.now());
    }

    public void incrementFailedLoginAttempts(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            
            // Lock account after 5 failed attempts for 30 minutes
            if (attempts >= 5) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
            }
            
            userRepository.save(user);
        }
    }

    public void resetFailedLoginAttempts(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            userRepository.save(user);
        }
    }

    public boolean isAccountLocked(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null && user.isAccountLocked();
    }

    public void unlockUser(Long userId) {
        userRepository.unlockUser(userId);
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
        
        // Deactivate all user sessions
        sessionRepository.deactivateAllUserSessions(user);
    }

    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
    }

    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void cleanupExpiredSessions() {
        sessionRepository.cleanupExpiredSessions(LocalDateTime.now());
    }

    public List<UserSession> getUserSessions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return sessionRepository.findByUser(user);
    }

    public void terminateSession(String sessionToken) {
        sessionRepository.deactivateSession(sessionToken);
    }

    public void terminateAllUserSessions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        sessionRepository.deactivateAllUserSessions(user);
    }
}