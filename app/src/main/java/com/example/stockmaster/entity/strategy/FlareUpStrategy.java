package com.example.stockmaster.entity.strategy;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.entity.k.K15Minutes;
import com.example.stockmaster.entity.k.K30Minutes;
import com.example.stockmaster.entity.k.K5Minutes;
import com.example.stockmaster.entity.k.K60Minutes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlareUpStrategy extends BaseStrategy{

    public FlareUpStrategy() {
        super(R.integer.strategyFlareUp);
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
//            Log.d("lwd", String.format("开始判断strategy klevel:%d, time:%s", k60StockForm.getkLevel(), k60StockForm.getTime()));
            for(StockForm stockForm : stockFormList){
                if(stockForm.getTime().after(startTime) && stockForm.getTime().before(endTime)){
//                    Log.d("lwd", String.format("合格 klevel：%d, time:%s", stockForm.getkLevel(), stockForm.getTime()));
                    kLevelSet.add(stockForm.getkLevel());
                }
//                kLevelSet.add(stockForm.getkLevel());
            }
            if (kLevelSet.size() == 4) {
                strategyAnalyseResultList.add(new StrategyAnalyseResult(stockId, k60StockForm.getPrice(), getStrategyId(), k60StockForm.getTime(), R.integer.typeStrategyBuy));
            }
        }
        return strategyAnalyseResultList;
    }
}
