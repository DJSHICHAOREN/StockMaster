package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.entity.form.StockForm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LongToArrangeStrategy extends BaseStrategy {

    public LongToArrangeStrategy(int strategyId) {
        super(strategyId);
    }

    @Override
    public List<StrategyAnalyseResult> analyse(List<StockForm> stockFormList, List<Date> dateList, String stockId) {
        List<StockForm> k60MinuteStockFormList = new ArrayList<>();
        // 挑选出符合条件的60K线
        for(StockForm stockForm : stockFormList){
            if(stockForm.getkLevel() == 60){
                k60MinuteStockFormList.add(stockForm);
            }
        }
        List<StrategyAnalyseResult> strategyAnalyseResultList = new ArrayList<>();
        for(StockForm k60StockForm : k60MinuteStockFormList){
            Set<Integer> kLevelSet = new HashSet<>();
            Date endTime = k60StockForm.getTime();
            Date startTime = getPreviousDate(dateList, endTime);
//            Log.d("lwd", String.format("开始判断strategy klevel:%d, time:%s", k60StockForm.getkLevel(), k60StockForm.getTime()));
            for(StockForm stockForm : stockFormList){
                if(stockForm.getTime().after(startTime) && stockForm.getTime().before(endTime)){
//                    Log.d("lwd", String.format("合格 klevel：%d, time:%s", stockForm.getkLevel(), stockForm.getTime()));
                    kLevelSet.add(stockForm.getkLevel());
                }
//                kLevelSet.add(stockForm.getkLevel());
            }
            if (kLevelSet.size() == 4) {
                strategyAnalyseResultList.add(new StrategyAnalyseResult(stockId, k60StockForm.getPrice(), STRATEGY_ID, k60StockForm.getTime(), 0));
            }
        }
        return strategyAnalyseResultList;
    }
}
