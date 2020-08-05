package com.example.stockmaster.entity.strategy;

import android.util.Log;
import android.util.TimeUtils;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.util.DateUtil;

import java.util.Date;

import static java.lang.Math.abs;

public class MinuteLongToArrangeStrategy extends BaseStrategy {

    StrategyResult lastStrategyResult = null;

    public MinuteLongToArrangeStrategy() {
        super(R.integer.strategyMinuteLongToArrange);
    }

    @Override
    public StrategyResult analyse(StockForm stockForm, Stock stock) {
        if(stockForm == null){
            return null;
        }

        StrategyResult strategyResult = null;
        if(stockForm.getFormId() == R.integer.formMinuteLongToArrange){
            if(lastStrategyResult == null || isTwoTimeSpaceIntervalBiggerThan(stockForm.getTime(), lastStrategyResult.getTime())){
                strategyResult = new StrategyResult(stock.getId(), stockForm.getPrice(), getStrategyId(), stockForm.getTime(), stockForm.getType());
                lastStrategyResult = strategyResult;

//                Log.d("lwd", strategyResult.toString());
            }
        }

        return strategyResult;
    }

    /**
     * 将时间放到StockForm中去寻找时间间隔
     * @param
     * @return
     */
    private boolean isTwoTimeSpaceIntervalBiggerThan(Date time1, Date time2){
        if(abs(time1.getDate() - time2.getDate()) > 0){
            return true;
        }

        float minuteInterval = Math.abs( time1.getTime() - time2.getTime() ) / (1000 * 60 * 60);
        Log.d("lwd", "minuteInterval:" + minuteInterval);
        if(minuteInterval > 2.5){
            return true;
        }
        return false;
    }
}
