package com.example.stockmaster.util;

import android.util.Log;

import com.example.stockmaster.entity.evaluate.SuccessResult;
import com.example.stockmaster.entity.strategy.StrategyResult;

import java.util.ArrayList;
import java.util.List;

public class SuccessRateAnalyser {
    private  static List<SuccessResult> successResultList = new ArrayList<>();

    public static void analyse(List<StrategyResult> strategyResultList){
        SuccessResult successResult = null;
        if(strategyResultList != null && strategyResultList.size() > 0){
            successResult = new SuccessResult(strategyResultList.get(0).getStockId());
        }
        else{
            return;
        }

        StrategyResult buyStrategy = null;
        StrategyResult saleStrategy = null;
        for (StrategyResult strategyResult: strategyResultList) {
            if(buyStrategy == null && strategyResult.getType()==0){
                buyStrategy = strategyResult;
            }
            else if(buyStrategy != null && strategyResult.getType()==1){
                saleStrategy = strategyResult;
                successResult.addBuyAndSaleStrategyResult(buyStrategy, saleStrategy);

                buyStrategy = null;
                saleStrategy = null;
            }
        }
        Log.d("lwd", successResult.toString());


    }
}
