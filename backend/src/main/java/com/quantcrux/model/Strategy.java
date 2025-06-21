package com.quantcrux.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "strategies")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Strategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Column(length = 1000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "strategy_assets", joinColumns = @JoinColumn(name = "strategy_id"))
    @Column(name = "asset")
    private List<String> assetList;

    @Embedded
    private Indicators indicators;

    @Embedded
    private Rules rules;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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

    public Indicators getIndicators() { return indicators; }
    public void setIndicators(Indicators indicators) { this.indicators = indicators; }

    public Rules getRules() { return rules; }
    public void setRules(Rules rules) { this.rules = rules; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Embeddable
    public static class Indicators {
        @Column(name = "ema_short")
        private Integer emaShort = 10;

        @Column(name = "ema_long")
        private Integer emaLong = 20;

        @Column(name = "rsi_period")
        private Integer rsiPeriod = 14;

        @Column(name = "macd_fast")
        private Integer macdFast = 12;

        @Column(name = "macd_slow")
        private Integer macdSlow = 26;

        @Column(name = "macd_signal")
        private Integer macdSignal = 9;

        // Getters and Setters
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
    }

    @Embeddable
    public static class Rules {
        @Column(name = "entry_condition")
        private String entryCondition = "ema_cross_up";

        @Column(name = "exit_condition")
        private String exitCondition = "ema_cross_down";

        @Column(name = "stop_loss")
        private Double stopLoss = 5.0;

        @Column(name = "take_profit")
        private Double takeProfit = 10.0;

        @Column(name = "position_size")
        private Double positionSize = 1.0;

        // Getters and Setters
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
}