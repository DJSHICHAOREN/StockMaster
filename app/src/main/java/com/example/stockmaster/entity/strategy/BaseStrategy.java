package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.form.StockForm;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseStrategy {
    private int strategyId;
    public List<StrategyResult> mStrategyResultList = new ArrayList<>();
    public BaseStrategy(int strategyId){
        this.strategyId = strategyId;
    }
    public abstract StrategyResult analyse(StockForm stockForm, Stock stock);

    public int getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }
}
