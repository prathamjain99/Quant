package com.quantcrux.repository;

import com.quantcrux.model.Strategy;
import com.quantcrux.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long> {
    
    List<Strategy> findByUser(User user);

    /**
     * Fetch strategies by user with eager loading of User to avoid lazy loading issues
     */
    @Query("SELECT s FROM Strategy s " +
           "JOIN FETCH s.user u " +
           "WHERE s.user = :user " +
           "ORDER BY s.createdAt DESC")
    List<Strategy> findByUserWithUser(@Param("user") User user);

    /**
     * Fetch a specific strategy with eager loading
     */
    @Query("SELECT s FROM Strategy s " +
           "JOIN FETCH s.user u " +
           "WHERE s.id = :strategyId AND s.user = :user")
    Optional<Strategy> findByIdAndUserWithUser(@Param("strategyId") Long strategyId, @Param("user") User user);

    /**
     * Find strategies by name pattern with eager loading
     */
    @Query("SELECT s FROM Strategy s " +
           "JOIN FETCH s.user u " +
           "WHERE s.user = :user AND LOWER(s.name) LIKE LOWER(CONCAT('%', :namePattern, '%')) " +
           "ORDER BY s.createdAt DESC")
    List<Strategy> findByUserAndNameContainingIgnoreCaseWithUser(@Param("user") User user, @Param("namePattern") String namePattern);

    /**
     * Count strategies by user
     */
    @Query("SELECT COUNT(s) FROM Strategy s WHERE s.user = :user")
    Long countByUser(@Param("user") User user);
}