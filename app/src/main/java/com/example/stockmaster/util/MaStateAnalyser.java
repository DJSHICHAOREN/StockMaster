package com.example.stockmaster.util;

import android.util.Log;

import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.entity.strategy.BaseStrategy;
import com.example.stockmaster.entity.strategy.StrategyAnalyseResult;
import com.example.stockmaster.entity.strategy.UpEmanativeStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MaStateAnalyser {
    private static MaStateAnalyser instance = null;
    private List<BaseStrategy> mBaseStrategyList = new ArrayList<>();
    public MaStateAnalyser(){
        mBaseStrategyList.add(new UpEmanativeStrategy());
    }

    public void analyse(String stockId, List<MaState> maStateList){
        for(BaseStrategy baseStrategy : mBaseStrategyList){
            StrategyAnalyseResult strategyAnalyseResult = baseStrategy.analyse(stockId, maStateList);
            if(strategyAnalyseResult != null){
                DBUtil.saveStrategyAnalyseResult(strategyAnalyseResult);
            }
        }
    }

    public static MaStateAnalyser getInstance(){
        if(instance == null){
            instance = new MaStateAnalyser();
        }
        return instance;
    }


}
