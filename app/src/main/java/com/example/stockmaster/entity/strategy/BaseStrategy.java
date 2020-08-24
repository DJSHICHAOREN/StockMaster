package com.example.stockmaster.entity.strategy;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.util.StockManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseStrategy {
    private int strategyId;
    public List<StrategyResult> mStrategyResultList = new ArrayList<>();
    public BaseStrategy(int strategyId){
        this.strategyId = strategyId;
    }

    public abstract StrategyResult analyse(StockForm stockForm, Stock stock);

    public int getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }


    StockForm lastStockForm = null;

    /**
     * 默认分析策略
     * @param stockForm
     * @param stock
     * @return
     */
    public StrategyResult analyse(StockForm stockForm, Stock stock, int formId, float hourIntervalThred) {
        if(stockForm == null){
            return null;
        }

        StrategyResult strategyResult = null;
        if(stockForm.getFormId() == formId){
//            Log.d("lwd", String.format("formMinuteLongToArrange time:%s, price:%s", stockForm.getTime(), stockForm.getPrice()));
            if(
                    (lastStockForm == null || isTwoTimeSpaceIntervalBiggerThan(stockForm.getTime(), lastStockForm.getTime(), hourIntervalThred))
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
    private boolean isTwoTimeSpaceIntervalBiggerThan(Date time1, Date time2, float hourIntervalThred){
        int dayIndex1 = StockManager.getDealDayList().indexOf(time1.getDate());
        int dayIndex2 = StockManager.getDealDayList().indexOf(time2.getDate());

        if(dayIndex1 == -1){
            dayIndex1 = StockManager.getDealDayList().size();
        }
        if(dayIndex2 == -1){
            dayIndex2 = StockManager.getDealDayList().size();
        }

        int dayGap = Math.abs(dayIndex1 - dayIndex2);

        double hourInterval = (time1.getTime() - time2.getTime() - 1000 * 60 * 60 * 17.5 * dayGap) / (1000 * 60 * 60);
//        Log.d("lwd",  String.format("time:%s dayGap:%d hourInterval:%s", time1.toString(), dayGap, hourInterval));
        if(hourInterval > hourIntervalThred){
//            Log.d("lwd", "return true");
            return true;
        }

        return false;
    }
}
