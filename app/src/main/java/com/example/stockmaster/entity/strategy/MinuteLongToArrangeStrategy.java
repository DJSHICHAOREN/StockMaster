package com.example.stockmaster.entity.strategy;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.util.StockManager;

import java.util.Date;

import static java.lang.Math.abs;

public class MinuteLongToArrangeStrategy extends BaseStrategy {

    StrategyResult lastStrategyResult = null;
    StockForm lastStockForm = null;

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
//            Log.d("lwd", String.format("formMinuteLongToArrange time:%s, price:%s", stockForm.getTime(), stockForm.getPrice()));
            if(
                    (lastStockForm == null || isTwoTimeSpaceIntervalBiggerThan(stockForm.getTime(), lastStockForm.getTime()))
                    && isPriceBiggerThan(stockForm.getPrice(), 2)
            ){
                strategyResult = new StrategyResult(stock.getId(), stockForm.getPrice(), getStrategyId(), stockForm.getTime(), stockForm.getType());
                Log.d("lwd", strategyResult.toString());
            }
            lastStockForm = stockForm;
        }
        return strategyResult;
    }

    private boolean isPriceBiggerThan(float price1, float price2){
        if(price1 > price2){
            return true;
        }
        return false;
    }

    /**
     * 将时间放到StockForm中去寻找时间间隔
     * @param
     * @return
     */
    private boolean isTwoTimeSpaceIntervalBiggerThan(Date time1, Date time2){
        int dayIndex1 = StockManager.getDealDayList().indexOf(time1.getDate());
        int dayIndex2 = StockManager.getDealDayList().indexOf(time2.getDate());

        if(dayIndex1 == -1){
            dayIndex1 = StockManager.getDealDayList().size();
        }
        if(dayIndex2 == -1){
            dayIndex2 = StockManager.getDealDayList().size();
        }

        int dayGap = Math.abs(dayIndex1 - dayIndex2);

        double minuteInterval = (time1.getTime() - time2.getTime() - 1000 * 60 * 60 * 17.5 * dayGap) / (1000 * 60 * 60);
//        Log.d("lwd",  String.format("time:%s dayGap:%d minuteInterval:%s", time1.toString(), dayGap, minuteInterval));
        if(minuteInterval > 2.5){
//            Log.d("lwd", "return true");
            return true;
        }

        return false;
    }
}
