package com.example.stockmaster.entity.strategy;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.form.StockForm;

import java.util.Date;
import java.util.List;

import static com.example.stockmaster.util.DateUtil.calculateMinutesGap;

public class VBBStrategy extends BaseStrategy {
    private StrategyResult previousStrategyResult;
    private Date previousBuyFormTime;


    public VBBStrategy() {
        super(R.integer.strategyVBB);
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

        // 添加买卖点
        // 出现多头向上形态
        StrategyResult strategyResult = null;
        if(stockForm.getFormId() == R.integer.formLongToArrange){
            // 之前没有买入或买入时间超过五分钟时
            if(previousBuyFormTime==null || calculateMinutesGap(previousBuyFormTime, stockForm.getTime()) > 5){
                strategyResult = new StrategyResult(stockId, stockForm.getPrice(), getStrategyId(), stockForm.getTime(), 0);
                mStrategyResultList.add(strategyResult);
                previousStrategyResult = strategyResult;
            }
            previousBuyFormTime = stockForm.getTime();
        }
        // 之前有买入点，且出现突破前低点形态时
        else if(previousStrategyResult != null && previousStrategyResult.getType() == 0 && stockForm.getFormId() == R.integer.formFallThroughSupport){
            strategyResult = new StrategyResult(stockId, stockForm.getPrice(), getStrategyId(), stockForm.getTime(), 1);
            mStrategyResultList.add(strategyResult);
            previousStrategyResult = strategyResult;
        }
        return strategyResult;
    }



    public List<StrategyResult> getmStrategyResultList() {
        return mStrategyResultList;
    }

    public void setmStrategyResultList(List<StrategyResult> mStrategyResultList) {
        this.mStrategyResultList = mStrategyResultList;
    }
}
