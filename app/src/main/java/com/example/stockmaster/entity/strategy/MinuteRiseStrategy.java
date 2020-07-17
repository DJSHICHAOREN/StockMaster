package com.example.stockmaster.entity.strategy;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.form.StockForm;

import java.util.Date;

import static com.example.stockmaster.util.DateUtil.calculateMinutesGap;

public class MinuteRiseStrategy extends BaseStrategy {
    private Date previousBuyFormTime;
    private boolean hasLongToArrangeFormBefore = false;
    public MinuteRiseStrategy() {
        super(R.integer.strategyMinuteRise);
    }

    @Override
    public StrategyResult analyse(StockForm stockForm, String stockId) {
        if(stockForm == null){
            Log.d("lwd", "VBBStrategy analyse stockFormList == null");
            return null;
        }
        if(stockForm.getkLevel() != 30){
            return null;
        }
        StrategyResult strategyResult = null;
        if(stockForm.getFormId() == R.integer.formMinuteRise && hasLongToArrangeFormBefore){
            if(previousBuyFormTime==null || calculateMinutesGap(previousBuyFormTime, stockForm.getTime()) > 5){
                strategyResult = new StrategyResult(stockId, stockForm.getPrice(), getStrategyId(), stockForm.getTime(), 0);
                mStrategyResultList.add(strategyResult);

                Log.d("lwd", strategyResult.toLongString());
            }
            previousBuyFormTime = stockForm.getTime();
        }
        else if(stockForm.getFormId() == R.integer.formLongToArrange){
            hasLongToArrangeFormBefore = true;
        }
        return strategyResult;
    }
}
