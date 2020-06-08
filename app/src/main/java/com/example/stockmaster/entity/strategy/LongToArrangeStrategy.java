package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.entity.form.StockForm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LongToArrangeStrategy extends BaseStrategy {

    private static int STRATEGY_ID = 1;

    public LongToArrangeStrategy() {
        super(STRATEGY_ID);
    }

    @Override
    public List<StrategyAnalyseResult> analyse(List<StockForm> stockFormList, List<Date> dateList, String stockId) {
        List<StrategyAnalyseResult> strategyAnalyseResultList = new ArrayList<>();
        // 挑选出符合条件的60K线
        for(StockForm stockForm : stockFormList){
            if(stockForm.getkLevel() == 30){
                strategyAnalyseResultList.add(new StrategyAnalyseResult(stockId, stockForm.getPrice(), STRATEGY_ID, stockForm.getTime(), 0));
            }
        }

        return strategyAnalyseResultList;
    }
}
