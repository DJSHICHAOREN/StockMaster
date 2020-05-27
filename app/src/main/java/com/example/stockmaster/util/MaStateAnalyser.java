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
    private List<BaseStrategy> mBaseStrategyList = new ArrayList<>();
    public MaStateAnalyser(String stockId, List<MaState> maStateList){
        mBaseStrategyList.add(new UpEmanativeStrategy(stockId, maStateList));
    }

    public void analyse(){
        for(BaseStrategy baseStrategy : mBaseStrategyList){
            StrategyAnalyseResult strategyAnalyseResult = baseStrategy.analyse();
        }
    }
}
