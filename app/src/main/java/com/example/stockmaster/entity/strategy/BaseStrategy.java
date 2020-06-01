package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.entity.form.StockForm;

import java.util.Date;
import java.util.List;

public abstract class BaseStrategy {
    private int strategyId;
    public BaseStrategy(int strategyId){
        this.strategyId = strategyId;
    }
    public abstract List<StrategyAnalyseResult> analyse(List<StockForm> stockFormList, List<Date> dateList, String stockId);
}
