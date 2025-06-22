package com.quantcrux.repository;

import com.quantcrux.model.Portfolio;
import com.quantcrux.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    /**
     * Find all portfolios for a specific user
     */
    @Query("SELECT p FROM Portfolio p " +
           "JOIN FETCH p.user u " +
           "WHERE p.user = :user AND p.isActive = true " +
           "ORDER BY p.updatedAt DESC")
    List<Portfolio> findByUserAndIsActiveTrueOrderByUpdatedAtDesc(@Param("user") User user);

    /**
     * Find a specific portfolio by ID and user with eager loading
     */
    @Query("SELECT p FROM Portfolio p " +
           "JOIN FETCH p.user u " +
           "LEFT JOIN FETCH p.trades t " +
           "LEFT JOIN FETCH t.product " +
           "WHERE p.id = :portfolioId AND p.user = :user AND p.isActive = true")
    Optional<Portfolio> findByIdAndUserWithTrades(@Param("portfolioId") Long portfolioId, @Param("user") User user);

    /**
     * Find a portfolio by ID and user without trades
     */
    @Query("SELECT p FROM Portfolio p " +
           "JOIN FETCH p.user u " +
           "WHERE p.id = :portfolioId AND p.user = :user AND p.isActive = true")
    Optional<Portfolio> findByIdAndUser(@Param("portfolioId") Long portfolioId, @Param("user") User user);

    /**
     * Check if a portfolio name already exists for a user
     */
    @Query("SELECT COUNT(p) > 0 FROM Portfolio p " +
           "WHERE p.user = :user AND LOWER(p.name) = LOWER(:name) AND p.isActive = true")
    boolean existsByUserAndNameIgnoreCase(@Param("user") User user, @Param("name") String name);

    /**
     * Check if a portfolio name exists for a user excluding a specific portfolio ID
     */
    @Query("SELECT COUNT(p) > 0 FROM Portfolio p " +
           "WHERE p.user = :user AND LOWER(p.name) = LOWER(:name) AND p.id != :portfolioId AND p.isActive = true")
    boolean existsByUserAndNameIgnoreCaseAndIdNot(@Param("user") User user, @Param("name") String name, @Param("portfolioId") Long portfolioId);

    /**
     * Count portfolios by user
     */
    @Query("SELECT COUNT(p) FROM Portfolio p WHERE p.user = :user AND p.isActive = true")
    Long countByUserAndIsActiveTrue(@Param("user") User user);

    /**
     * Find portfolios that need metric recalculation
     */
    @Query("SELECT p FROM Portfolio p " +
           "WHERE p.isActive = true AND " +
           "(p.lastCalculated IS NULL OR p.lastCalculated < :threshold)")
    List<Portfolio> findPortfoliosNeedingRecalculation(@Param("threshold") LocalDateTime threshold);

    /**
     * Find portfolios by name pattern for a user
     */
    @Query("SELECT p FROM Portfolio p " +
           "JOIN FETCH p.user u " +
           "WHERE p.user = :user AND LOWER(p.name) LIKE LOWER(CONCAT('%', :namePattern, '%')) " +
           "AND p.isActive = true " +
           "ORDER BY p.updatedAt DESC")
    List<Portfolio> findByUserAndNameContainingIgnoreCase(@Param("user") User user, @Param("namePattern") String namePattern);

    /**
     * Get portfolio statistics for a user
     */
    @Query("SELECT " +
           "COUNT(p) as totalPortfolios, " +
           "SUM(p.totalValue) as totalValue, " +
           "AVG(p.pnlPercentage) as avgPnlPercentage, " +
           "SUM(p.positionCount) as totalPositions " +
           "FROM Portfolio p " +
           "WHERE p.user = :user AND p.isActive = true")
    Object[] getPortfolioStatsByUser(@Param("user") User user);
}