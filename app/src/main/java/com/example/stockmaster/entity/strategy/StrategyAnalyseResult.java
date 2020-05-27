package com.example.stockmaster.entity.strategy;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

@Table(name = "StrategyAnalyseResult")
public class StrategyAnalyseResult {

    @Column(name = "stockId")
    public String stockId;

    @Column(name = "strategyId")
    public int strategyId;

    @Column(name = "time")
    public Date time;

    public StrategyAnalyseResult(String stockId, int strategyId, Date time){
        this.stockId = stockId;
        this.strategyId = strategyId;
        this.time = time;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public int getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
