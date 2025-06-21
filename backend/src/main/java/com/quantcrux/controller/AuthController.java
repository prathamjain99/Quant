package com.quantcrux.controller;

import com.quantcrux.dto.JwtResponse;
import com.quantcrux.dto.LoginRequest;
import com.quantcrux.dto.UserRegistrationRequest;
import com.quantcrux.dto.MessageResponse;
import com.quantcrux.dto.UserProfileResponse;
import com.quantcrux.model.User;
import com.quantcrux.model.UserSession;
import com.quantcrux.repository.UserRepository;
import com.quantcrux.security.JwtTokenProvider;
import com.quantcrux.service.SessionService;
import com.quantcrux.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, 
                                            HttpServletRequest request) {
        try {
            // Check if account is locked
            if (userService.isAccountLocked(loginRequest.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Account is temporarily locked due to multiple failed login attempts. Please try again later."));
            }

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), 
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            String jwt = jwtTokenProvider.generateToken(authentication);

            // Get user details
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Reset failed login attempts on successful login
            userService.resetFailedLoginAttempts(loginRequest.getUsername());

            // Update last login time
            userService.updateLastLogin(user.getId());

            // Create session
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            UserSession session = sessionService.createSession(user, jwt, ipAddress, userAgent);

            // Prepare user info for response
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId().toString());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("name", user.getName());
            userInfo.put("role", user.getRole().name().toLowerCase());
            userInfo.put("isActive", user.getIsActive());
            userInfo.put("isEmailVerified", user.getIsEmailVerified());
            userInfo.put("lastLogin", user.getLastLogin());

            // Prepare response with session info
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("type", "Bearer");
            response.put("refreshToken", session.getRefreshToken());
            response.put("user", userInfo);
            response.put("expiresAt", session.getExpiresAt());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            // Increment failed login attempts
            userService.incrementFailedLoginAttempts(loginRequest.getUsername());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid username or password"));
        } catch (LockedException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Account is locked. Please contact administrator."));
        } catch (DisabledException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Account is disabled. Please contact administrator."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest signUpRequest,
                                        Authentication authentication) {
        try {
            // Determine who is creating the user
            String createdBy = authentication != null ? authentication.getName() : "system";
            
            // Create user
            User user = userService.createUser(signUpRequest, createdBy);

            return ResponseEntity.ok(new MessageResponse("User registered successfully! User ID: " + user.getId()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Registration failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Refresh token is required"));
            }

            // Find session by refresh token
            UserSession session = sessionService.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

            // Generate new JWT token
            User user = session.getUser();
            String newJwt = jwtTokenProvider.generateTokenForUser(user);

            // Update session with new token
            UserSession updatedSession = sessionService.refreshSession(refreshToken, newJwt);

            Map<String, Object> response = new HashMap<>();
            response.put("token", newJwt);
            response.put("type", "Bearer");
            response.put("expiresAt", updatedSession.getExpiresAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Token refresh failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        try {
            String jwt = jwtTokenProvider.getJwtFromRequest(request);
            
            if (jwt != null) {
                sessionService.invalidateSession(jwt);
            }

            return ResponseEntity.ok(new MessageResponse("User logged out successfully"));

        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse("Logout completed"));
        }
    }

    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAllSessions(Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            sessionService.invalidateAllUserSessions(user);

            return ResponseEntity.ok(new MessageResponse("All sessions terminated successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Failed to terminate sessions: " + e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(Authentication authentication) {
        try {
            UserProfileResponse profile = userService.getUserProfile(authentication.getName());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfileResponse profileUpdate,
                                             Authentication authentication) {
        try {
            userService.updateUserProfile(authentication.getName(), profileUpdate);
            return ResponseEntity.ok(new MessageResponse("Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Profile update failed: " + e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request,
                                          Authentication authentication) {
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Old password and new password are required"));
            }

            userService.changePassword(authentication.getName(), oldPassword, newPassword);

            return ResponseEntity.ok(new MessageResponse("Password changed successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Password change failed: " + e.getMessage()));
        }
    }

    @GetMapping("/sessions")
    public ResponseEntity<?> getUserSessions(Authentication authentication) {
        try {
            return ResponseEntity.ok(userService.getUserSessions(authentication.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Failed to retrieve sessions: " + e.getMessage()));
        }
    }

    @DeleteMapping("/sessions/{sessionToken}")
    public ResponseEntity<?> terminateSession(@PathVariable String sessionToken,
                                            Authentication authentication) {
        try {
            userService.terminateSession(sessionToken);
            return ResponseEntity.ok(new MessageResponse("Session terminated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Failed to terminate session: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "auth-service");
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String jwt = jwtTokenProvider.getJwtFromRequest(request);
            
            if (jwt != null && jwtTokenProvider.validateToken(jwt) && sessionService.isSessionValid(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId().toString());
                userInfo.put("username", user.getUsername());
                userInfo.put("role", user.getRole().name().toLowerCase());
                userInfo.put("isActive", user.getIsActive());

                return ResponseEntity.ok(Map.of("valid", true, "user", userInfo));
            }

            return ResponseEntity.ok(Map.of("valid", false));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false, "error", e.getMessage()));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}