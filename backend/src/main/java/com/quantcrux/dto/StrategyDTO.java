package com.quantcrux.dto;

import com.quantcrux.model.Strategy;
import java.time.LocalDateTime;
import java.util.List;

public class StrategyDTO {
    private Long id;
    private String name;
    private String description;
    private List<String> assetList;
    private LocalDateTime createdAt;
    private String createdByUser;
    
    // Indicators
    private Integer emaShort;
    private Integer emaLong;
    private Integer rsiPeriod;
    private Integer macdFast;
    private Integer macdSlow;
    private Integer macdSignal;
    
    // Rules
    private String entryCondition;
    private String exitCondition;
    private Double stopLoss;
    private Double takeProfit;
    private Double positionSize;

    // Default constructor
    public StrategyDTO() {}

    // Static factory method from Strategy entity
    public static StrategyDTO fromStrategy(Strategy strategy) {
        StrategyDTO dto = new StrategyDTO();
        dto.setId(strategy.getId());
        dto.setName(strategy.getName());
        dto.setDescription(strategy.getDescription());
        dto.setAssetList(strategy.getAssetList());
        dto.setCreatedAt(strategy.getCreatedAt());
        dto.setCreatedByUser(strategy.getUser().getName());
        
        // Map indicators
        if (strategy.getIndicators() != null) {
            dto.setEmaShort(strategy.getIndicators().getEmaShort());
            dto.setEmaLong(strategy.getIndicators().getEmaLong());
            dto.setRsiPeriod(strategy.getIndicators().getRsiPeriod());
            dto.setMacdFast(strategy.getIndicators().getMacdFast());
            dto.setMacdSlow(strategy.getIndicators().getMacdSlow());
            dto.setMacdSignal(strategy.getIndicators().getMacdSignal());
        }
        
        // Map rules
        if (strategy.getRules() != null) {
            dto.setEntryCondition(strategy.getRules().getEntryCondition());
            dto.setExitCondition(strategy.getRules().getExitCondition());
            dto.setStopLoss(strategy.getRules().getStopLoss());
            dto.setTakeProfit(strategy.getRules().getTakeProfit());
            dto.setPositionSize(strategy.getRules().getPositionSize());
        }
        
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getAssetList() { return assetList; }
    public void setAssetList(List<String> assetList) { this.assetList = assetList; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(String createdByUser) { this.createdByUser = createdByUser; }

    public Integer getEmaShort() { return emaShort; }
    public void setEmaShort(Integer emaShort) { this.emaShort = emaShort; }

    public Integer getEmaLong() { return emaLong; }
    public void setEmaLong(Integer emaLong) { this.emaLong = emaLong; }

    public Integer getRsiPeriod() { return rsiPeriod; }
    public void setRsiPeriod(Integer rsiPeriod) { this.rsiPeriod = rsiPeriod; }

    public Integer getMacdFast() { return macdFast; }
    public void setMacdFast(Integer macdFast) { this.macdFast = macdFast; }

    public Integer getMacdSlow() { return macdSlow; }
    public void setMacdSlow(Integer macdSlow) { this.macdSlow = macdSlow; }

    public Integer getMacdSignal() { return macdSignal; }
    public void setMacdSignal(Integer macdSignal) { this.macdSignal = macdSignal; }

    public String getEntryCondition() { return entryCondition; }
    public void setEntryCondition(String entryCondition) { this.entryCondition = entryCondition; }

    public String getExitCondition() { return exitCondition; }
    public void setExitCondition(String exitCondition) { this.exitCondition = exitCondition; }

    public Double getStopLoss() { return stopLoss; }
    public void setStopLoss(Double stopLoss) { this.stopLoss = stopLoss; }

    public Double getTakeProfit() { return takeProfit; }
    public void setTakeProfit(Double takeProfit) { this.takeProfit = takeProfit; }

    public Double getPositionSize() { return positionSize; }
    public void setPositionSize(Double positionSize) { this.positionSize = positionSize; }
}