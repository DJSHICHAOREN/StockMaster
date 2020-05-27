package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.entity.ma.MaState;

import java.util.List;

public abstract class BaseStrategy {
    private int strategyId;
    private String stockId;
    protected List<MaState> maStateList;
    protected boolean isPrintBeginAnalyseTime = true;
    public BaseStrategy(int strategyId, String stockId, List<MaState> maStateList){
        this.strategyId = strategyId;
        this.stockId = stockId;
        this.maStateList = maStateList;
    }

    public abstract StrategyAnalyseResult analyse();

    public int getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }

    public List<MaState> getMaStateList() {
        return maStateList;
    }

    public void setMaStateList(List<MaState> maStateList) {
        this.maStateList = maStateList;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }
}
