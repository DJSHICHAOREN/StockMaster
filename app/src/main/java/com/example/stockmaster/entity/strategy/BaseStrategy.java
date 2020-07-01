package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.entity.form.StockForm;

import java.util.Date;
import java.util.List;

public abstract class BaseStrategy {
    private int strategyId;
    public BaseStrategy(int strategyId){
        this.strategyId = strategyId;
    }
    public abstract StrategyResult analyse(StockForm stockForm, String stockId);

    public int getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }
}
