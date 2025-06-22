package com.quantcrux.repository;

import com.quantcrux.model.StrategyV2;
import com.quantcrux.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StrategyV2Repository extends JpaRepository<StrategyV2, Long> {
    
    /**
     * Find strategies by owner with eager loading
     */
    @Query("SELECT s FROM StrategyV2 s " +
           "JOIN FETCH s.owner o " +
           "WHERE s.owner = :owner " +
           "ORDER BY s.updatedAt DESC")
    List<StrategyV2> findByOwnerWithOwner(@Param("owner") User owner);

    /**
     * Find all public strategies with eager loading
     */
    @Query("SELECT s FROM StrategyV2 s " +
           "JOIN FETCH s.owner o " +
           "WHERE s.isPublic = true " +
           "ORDER BY s.publishedAt DESC, s.updatedAt DESC")
    List<StrategyV2> findPublicStrategiesWithOwner();

    /**
     * Find all strategies (for portfolio managers) with eager loading
     */
    @Query("SELECT s FROM StrategyV2 s " +
           "JOIN FETCH s.owner o " +
           "ORDER BY s.updatedAt DESC")
    List<StrategyV2> findAllWithOwner();

    /**
     * Find a specific strategy by ID and owner with eager loading
     */
    @Query("SELECT s FROM StrategyV2 s " +
           "JOIN FETCH s.owner o " +
           "WHERE s.id = :strategyId AND s.owner = :owner")
    Optional<StrategyV2> findByIdAndOwnerWithOwner(@Param("strategyId") Long strategyId, @Param("owner") User owner);

    /**
     * Find a specific strategy by ID with eager loading
     */
    @Query("SELECT s FROM StrategyV2 s " +
           "JOIN FETCH s.owner o " +
           "WHERE s.id = :strategyId")
    Optional<StrategyV2> findByIdWithOwner(@Param("strategyId") Long strategyId);

    /**
     * Check if a strategy name already exists for a user
     */
    @Query("SELECT COUNT(s) > 0 FROM StrategyV2 s " +
           "WHERE s.owner = :owner AND LOWER(s.name) = LOWER(:name)")
    boolean existsByOwnerAndNameIgnoreCase(@Param("owner") User owner, @Param("name") String name);

    /**
     * Check if a strategy name exists for a user excluding a specific strategy ID
     */
    @Query("SELECT COUNT(s) > 0 FROM StrategyV2 s " +
           "WHERE s.owner = :owner AND LOWER(s.name) = LOWER(:name) AND s.id != :strategyId")
    boolean existsByOwnerAndNameIgnoreCaseAndIdNot(@Param("owner") User owner, @Param("name") String name, @Param("strategyId") Long strategyId);

    /**
     * Find strategies by tags
     */
    @Query("SELECT s FROM StrategyV2 s " +
           "JOIN FETCH s.owner o " +
           "WHERE :tag = ANY(s.tags) " +
           "ORDER BY s.updatedAt DESC")
    List<StrategyV2> findByTagsContaining(@Param("tag") String tag);

    /**
     * Find public strategies by tags
     */
    @Query("SELECT s FROM StrategyV2 s " +
           "JOIN FETCH s.owner o " +
           "WHERE s.isPublic = true AND :tag = ANY(s.tags) " +
           "ORDER BY s.publishedAt DESC, s.updatedAt DESC")
    List<StrategyV2> findPublicStrategiesByTagsContaining(@Param("tag") String tag);

    /**
     * Search strategies by name pattern for a user
     */
    @Query("SELECT s FROM StrategyV2 s " +
           "JOIN FETCH s.owner o " +
           "WHERE s.owner = :owner AND LOWER(s.name) LIKE LOWER(CONCAT('%', :namePattern, '%')) " +
           "ORDER BY s.updatedAt DESC")
    List<StrategyV2> findByOwnerAndNameContainingIgnoreCase(@Param("owner") User owner, @Param("namePattern") String namePattern);

    /**
     * Search public strategies by name pattern
     */
    @Query("SELECT s FROM StrategyV2 s " +
           "JOIN FETCH s.owner o " +
           "WHERE s.isPublic = true AND LOWER(s.name) LIKE LOWER(CONCAT('%', :namePattern, '%')) " +
           "ORDER BY s.publishedAt DESC, s.updatedAt DESC")
    List<StrategyV2> findPublicStrategiesByNameContainingIgnoreCase(@Param("namePattern") String namePattern);

    /**
     * Count strategies by owner
     */
    @Query("SELECT COUNT(s) FROM StrategyV2 s WHERE s.owner = :owner")
    Long countByOwner(@Param("owner") User owner);

    /**
     * Count public strategies by owner
     */
    @Query("SELECT COUNT(s) FROM StrategyV2 s WHERE s.owner = :owner AND s.isPublic = true")
    Long countPublicByOwner(@Param("owner") User owner);

    /**
     * Get strategy statistics for a user
     */
    @Query("SELECT " +
           "COUNT(s) as totalStrategies, " +
           "COUNT(CASE WHEN s.isPublic = true THEN 1 END) as publicStrategies, " +
           "COUNT(CASE WHEN s.isPublic = false THEN 1 END) as privateStrategies " +
           "FROM StrategyV2 s " +
           "WHERE s.owner = :owner")
    Object[] getStrategyStatsByOwner(@Param("owner") User owner);
}