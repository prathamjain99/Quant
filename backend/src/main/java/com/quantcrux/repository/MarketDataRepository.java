package com.quantcrux.repository;

import com.quantcrux.model.MarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketData, Long> {
    
    Optional<MarketData> findBySymbol(String symbol);
    
    List<MarketData> findByIsActiveTrueOrderBySymbol();
    
    List<MarketData> findByExchangeAndIsActiveTrueOrderBySymbol(String exchange);
    
    List<MarketData> findBySectorAndIsActiveTrueOrderBySymbol(String sector);
    
    @Query("SELECT m FROM MarketData m WHERE m.symbol IN :symbols AND m.isActive = true ORDER BY m.symbol")
    List<MarketData> findBySymbolsAndIsActiveTrue(@Param("symbols") List<String> symbols);
    
    @Query("SELECT m FROM MarketData m WHERE m.isActive = true AND m.updatedAt >= :since ORDER BY m.updatedAt DESC")
    List<MarketData> findRecentlyUpdated(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(m) FROM MarketData m WHERE m.isActive = true")
    Long countActiveMarkets();
    
    @Query("SELECT m FROM MarketData m WHERE m.changePercent > :threshold AND m.isActive = true ORDER BY m.changePercent DESC")
    List<MarketData> findTopGainers(@Param("threshold") Double threshold);
    
    @Query("SELECT m FROM MarketData m WHERE m.changePercent < :threshold AND m.isActive = true ORDER BY m.changePercent ASC")
    List<MarketData> findTopLosers(@Param("threshold") Double threshold);
}