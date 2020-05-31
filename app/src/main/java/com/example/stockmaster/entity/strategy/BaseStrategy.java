package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.entity.ma.MaState;

import java.util.List;

public abstract class BaseStrategy {
    private int strategyId;
    protected boolean isPrintBeginAnalyseTime = true;
    public BaseStrategy(int strategyId){
        this.strategyId = strategyId;
    }

    public abstract StrategyAnalyseResult analyse(String stockId, List<MaState> maStateList, int kLevel);

    public int getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }
}
