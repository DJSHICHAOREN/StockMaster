package com.example.stockmaster.entity.strategy;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.form.StockForm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VBBStrategy extends BaseStrategy {
    private StrategyResult previousStrategyResult;
    private Date previousBuyFormTime;
    public VBBStrategy() {
        super(R.integer.strategyFallThroughSupport);
    }

    @Override
    public List<StrategyResult> analyse(List<StockForm> stockFormList, List<Date> dateList, String stockId) {
        if(stockFormList == null){
            Log.d("lwd", "VBBStrategy analyse stockFormList == null");
            return new ArrayList<>();
        }

        List<StrategyResult> strategyResultList = new ArrayList<>();
        // 若之前有买点，则写入卖出策略
        for(StockForm stockForm : stockFormList){
            if(stockForm.getkLevel() != 30){
                continue;
            }
            // 添加买卖点
            if(stockForm.getFormId() == R.integer.formLongToArrange){
                if(previousBuyFormTime==null || calculateMinutesGap(previousBuyFormTime, stockForm.getTime()) > 5){
                    StrategyResult strategyResult = new StrategyResult(stockId, stockForm.getPrice(), getStrategyId(), stockForm.getTime(), 0);
                    strategyResultList.add(strategyResult);
                    previousStrategyResult = strategyResult;
                }
                previousBuyFormTime = stockForm.getTime();
            }
            else if(previousStrategyResult != null && previousStrategyResult.getType() == 0 && stockForm.getFormId() == R.integer.formFallThroughSupport){
                StrategyResult strategyResult = new StrategyResult(stockId, stockForm.getPrice(), getStrategyId(), stockForm.getTime(), 1);
                strategyResultList.add(strategyResult);
                previousStrategyResult = strategyResult;
            }
        }
        return strategyResultList;
    }

    public static long calculateMinutesGap(Date date1, Date date2){
        long gapMinutes = (date2.getTime() - date1.getTime())/(1000 * 60);
        gapMinutes = Math.abs(gapMinutes);
        return gapMinutes;
    }

}
