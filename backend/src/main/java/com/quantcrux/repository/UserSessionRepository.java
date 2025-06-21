package com.quantcrux.repository;

import com.quantcrux.model.User;
import com.quantcrux.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findBySessionToken(String sessionToken);
    Optional<UserSession> findByRefreshToken(String refreshToken);
    
    List<UserSession> findByUserAndIsActiveTrue(User user);
    List<UserSession> findByUser(User user);
    
    @Query("SELECT s FROM UserSession s WHERE s.expiresAt < :now")
    List<UserSession> findExpiredSessions(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.user = :user")
    void deactivateAllUserSessions(@Param("user") User user);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.sessionToken = :token")
    void deactivateSession(@Param("token") String token);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.lastAccessed = :accessTime WHERE s.sessionToken = :token")
    void updateLastAccessed(@Param("token") String token, @Param("accessTime") LocalDateTime accessTime);
    
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.expiresAt < :now OR s.isActive = false")
    void cleanupExpiredSessions(@Param("now") LocalDateTime now);
    
    Long countByUserAndIsActiveTrue(User user);
}