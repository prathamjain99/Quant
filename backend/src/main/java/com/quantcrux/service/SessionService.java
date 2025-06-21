package com.quantcrux.service;

import com.quantcrux.model.User;
import com.quantcrux.model.UserSession;
import com.quantcrux.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SessionService {

    @Autowired
    private UserSessionRepository sessionRepository;

    @Value("${jwt.expiration:86400000}") // 24 hours default
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private long refreshExpirationMs;

    public UserSession createSession(User user, String sessionToken, String ipAddress, String userAgent) {
        // Generate refresh token
        String refreshToken = UUID.randomUUID().toString();
        
        // Calculate expiration times
        LocalDateTime sessionExpiry = LocalDateTime.now().plusSeconds(jwtExpirationMs / 1000);
        
        UserSession session = new UserSession(user, sessionToken, refreshToken, ipAddress, userAgent, sessionExpiry);
        return sessionRepository.save(session);
    }

    public Optional<UserSession> findBySessionToken(String sessionToken) {
        return sessionRepository.findBySessionToken(sessionToken);
    }

    public Optional<UserSession> findByRefreshToken(String refreshToken) {
        return sessionRepository.findByRefreshToken(refreshToken);
    }

    public boolean isSessionValid(String sessionToken) {
        Optional<UserSession> session = sessionRepository.findBySessionToken(sessionToken);
        if (session.isPresent()) {
            UserSession userSession = session.get();
            if (userSession.getIsActive() && !userSession.isExpired()) {
                // Update last accessed time
                updateLastAccessed(sessionToken);
                return true;
            }
        }
        return false;
    }

    public void updateLastAccessed(String sessionToken) {
        sessionRepository.updateLastAccessed(sessionToken, LocalDateTime.now());
    }

    public void invalidateSession(String sessionToken) {
        sessionRepository.deactivateSession(sessionToken);
    }

    public void invalidateAllUserSessions(User user) {
        sessionRepository.deactivateAllUserSessions(user);
    }

    public UserSession refreshSession(String refreshToken, String newSessionToken) {
        Optional<UserSession> sessionOpt = sessionRepository.findByRefreshToken(refreshToken);
        
        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            
            if (session.getIsActive() && !session.isExpired()) {
                // Update session with new token and extend expiry
                session.setSessionToken(newSessionToken);
                session.setExpiresAt(LocalDateTime.now().plusSeconds(jwtExpirationMs / 1000));
                session.setLastAccessed(LocalDateTime.now());
                
                return sessionRepository.save(session);
            }
        }
        
        throw new RuntimeException("Invalid or expired refresh token");
    }

    public void cleanupExpiredSessions() {
        sessionRepository.cleanupExpiredSessions(LocalDateTime.now());
    }

    public long getActiveSessionCount(User user) {
        return sessionRepository.countByUserAndIsActiveTrue(user);
    }
}