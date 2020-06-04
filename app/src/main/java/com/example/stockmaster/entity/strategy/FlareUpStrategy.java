package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.entity.form.StockForm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlareUpStrategy extends BaseStrategy{

    private static int STRATEGY_ID = 0;
    public FlareUpStrategy() {
        super(STRATEGY_ID);
    }

    /**
     * 得到dateList中nowDate前一天的的日期
     * 如果nowDate没有前一天，则返回nowDate
     * @param dateList
     * @param nowDate
     * @return
     */
    public Date getPreviousDate(List<Date> dateList, Date nowDate){
        int nowDateIndex = -1;
        for(int i=0; i<dateList.size(); i++){
            if(nowDate.getDate() == dateList.get(i).getDate()){
                nowDateIndex = i;
            }
        }
        if(nowDateIndex > 0){
            return dateList.get(nowDateIndex-1);
        }
        return nowDate;
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
            for(StockForm stockForm : stockFormList){
                if(stockForm.getTime().after(startTime) && stockForm.getTime().before(endTime)){
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