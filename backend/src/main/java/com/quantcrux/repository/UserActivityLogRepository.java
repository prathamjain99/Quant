package com.quantcrux.repository;

import com.quantcrux.model.User;
import com.quantcrux.model.UserActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    
    List<UserActivityLog> findByUserOrderByCreatedAtDesc(User user);
    
    Page<UserActivityLog> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<UserActivityLog> findByUserAndActivityTypeOrderByCreatedAtDesc(User user, String activityType);
    
    @Query("SELECT a FROM UserActivityLog a WHERE a.user = :user AND a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<UserActivityLog> findRecentActivities(@Param("user") User user, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(a) FROM UserActivityLog a WHERE a.user = :user AND a.activityType = :activityType")
    Long countByUserAndActivityType(@Param("user") User user, @Param("activityType") String activityType);
    
    @Query("SELECT a FROM UserActivityLog a WHERE a.user = :user AND a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.createdAt DESC")
    List<UserActivityLog> findByUserAndEntity(@Param("user") User user, @Param("entityType") String entityType, @Param("entityId") Long entityId);
    
    @Query("SELECT COUNT(a) FROM UserActivityLog a WHERE a.createdAt >= :since")
    Long countActivitiesSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT a.activityType, COUNT(a) FROM UserActivityLog a WHERE a.user = :user GROUP BY a.activityType")
    List<Object[]> getActivityStatsByUser(@Param("user") User user);
}