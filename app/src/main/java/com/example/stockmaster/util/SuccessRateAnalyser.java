package com.example.stockmaster.util;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.evaluate.SuccessResult;
import com.example.stockmaster.entity.strategy.StrategyResult;

import java.util.ArrayList;
import java.util.List;

public class SuccessRateAnalyser {
    private static List<SuccessResult> successResultList = new ArrayList<>();

    public static void analyse(List<StrategyResult> strategyResultList){
        if(strategyResultList == null || strategyResultList.size() == 0){
            return;
        }
        SuccessResult successResult = new SuccessResult(strategyResultList.get(0).getStockId());
        StrategyResult buyStrategyResult = null;
        StrategyResult saleStrategyResult = null;
        for (StrategyResult strategyResult: strategyResultList) {
            if(strategyResult.getStrategyId() != R.integer.strategyMinuteRise){
                continue;
            }
            if(buyStrategyResult == null && strategyResult.getType()==0){
                buyStrategyResult = strategyResult;
            }
            else if(buyStrategyResult != null && strategyResult.getType()==1){
                saleStrategyResult = strategyResult;
                successResult.addBuyAndSaleStrategyResult(buyStrategyResult, saleStrategyResult);

                buyStrategyResult = null;
                saleStrategyResult = null;
            }
        }
        Log.d("lwd", successResult.toString());
    }
}
