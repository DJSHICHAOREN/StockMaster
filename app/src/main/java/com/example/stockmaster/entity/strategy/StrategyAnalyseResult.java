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

    @Column(name = "type")
    public int type; // 类型：购买：0，卖出：1

    public StrategyAnalyseResult(String stockId, int strategyId, Date time, int type){
        this.stockId = stockId;
        this.strategyId = strategyId;
        this.time = time;
        this.type = type;
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
