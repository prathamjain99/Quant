package com.quantcrux.repository;

import com.quantcrux.dto.TradeDTO;
import com.quantcrux.model.Trade;
import com.quantcrux.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    
    List<Trade> findByUser(User user);
    
    Optional<Trade> findByIdAndUser(Long id, User user);

    /**
     * Fetch trades with eager loading of Product and User to avoid lazy loading issues
     */
    @Query("SELECT t FROM Trade t " +
           "JOIN FETCH t.product p " +
           "JOIN FETCH t.user u " +
           "WHERE t.user = :user " +
           "ORDER BY t.tradeDate DESC")
    List<Trade> findByUserWithProductAndUser(@Param("user") User user);

    /**
     * Fetch a specific trade with eager loading
     */
    @Query("SELECT t FROM Trade t " +
           "JOIN FETCH t.product p " +
           "JOIN FETCH t.user u " +
           "WHERE t.id = :tradeId AND t.user = :user")
    Optional<Trade> findByIdAndUserWithProduct(@Param("tradeId") Long tradeId, @Param("user") User user);

    /**
     * Alternative approach using JPQL projection to create DTOs directly
     */
    @Query("SELECT new com.quantcrux.dto.TradeDTO(" +
           "t.id, p.name, p.type, p.underlyingAsset, t.tradeType, " +
           "CAST(t.status AS string), t.notional, t.entryPrice, t.currentPrice, " +
           "t.notes, t.tradeDate, u.name) " +
           "FROM Trade t " +
           "JOIN t.product p " +
           "JOIN t.user u " +
           "WHERE t.user = :user " +
           "ORDER BY t.tradeDate DESC")
    List<TradeDTO> findTradeProjectionsByUser(@Param("user") User user);

    /**
     * Get trades by status with eager loading
     */
    @Query("SELECT t FROM Trade t " +
           "JOIN FETCH t.product p " +
           "JOIN FETCH t.user u " +
           "WHERE t.user = :user AND t.status = :status " +
           "ORDER BY t.tradeDate DESC")
    List<Trade> findByUserAndStatusWithProductAndUser(@Param("user") User user, @Param("status") Trade.TradeStatus status);

    /**
     * Count trades by user
     */
    @Query("SELECT COUNT(t) FROM Trade t WHERE t.user = :user")
    Long countByUser(@Param("user") User user);

    /**
     * Get recent trades with limit
     */
    @Query("SELECT t FROM Trade t " +
           "JOIN FETCH t.product p " +
           "JOIN FETCH t.user u " +
           "WHERE t.user = :user " +
           "ORDER BY t.tradeDate DESC " +
           "LIMIT :limit")
    List<Trade> findRecentTradesByUser(@Param("user") User user, @Param("limit") int limit);
}