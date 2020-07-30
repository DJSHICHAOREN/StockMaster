package com.example.stockmaster.entity.strategy;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.form.StockForm;

import java.util.Date;

import static com.example.stockmaster.util.DateUtil.calculateMinutesGap;

public class MinuteRiseStrategy extends BaseStrategy {
    private Date previousBuyFormTime;
    public MinuteRiseStrategy() {
        super(R.integer.strategyMinuteRise);
    }

    @Override
    public StrategyResult analyse(StockForm stockForm, Stock stock) {
        if(stockForm == null){
            Log.d("lwd", "VBBStrategy analyse stockFormList == null");
            return null;
        }
        if(stockForm.getkLevel() != 30){
            return null;
        }
        StrategyResult strategyResult = null;
        if(stockForm.getFormId() == R.integer.formMinuteRise){
            if(stockForm.getType() == 0 && stock.getMonitorType() != 1){
                return null;
            }
            else if(stockForm.getType() == 1 && stock.getMonitorType() != 2){
                return null;
            }

            if(previousBuyFormTime==null || calculateMinutesGap(previousBuyFormTime, stockForm.getTime()) > 5){
                strategyResult = new StrategyResult(stock.getId(), stockForm.getPrice(), getStrategyId(), stockForm.getTime(), 0);
                mStrategyResultList.add(strategyResult);
            }
            previousBuyFormTime = stockForm.getTime();
        }
        return strategyResult;
    }
}
