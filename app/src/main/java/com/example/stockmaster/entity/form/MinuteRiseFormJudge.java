package com.example.stockmaster.entity.form;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.entity.strategy.BaseStrategy;
import com.example.stockmaster.entity.strategy.StrategyResult;

import java.util.List;

public class MinuteRiseFormJudge extends BaseFormJudge {

    public MinuteRiseFormJudge() {
        super(R.integer.formMinuteRise);
    }

    @Override
    public StockForm judge(String stockId, List<MaState> maStateList, int kLevel, Stock stock, List<StockPrice> stockPriceList) {
        if(maStateList == null || maStateList.size() < 1){
            return null;
        }
        MaState lastMaState1 = maStateList.get(maStateList.size()-1);
        if(lastMaState1.getMinPriceInOneHour() == -1){
            return null;
        }
        if(lastMaState1.getPrice() >= lastMaState1.getMinPriceInOneHour() * 1.03){
            return new StockForm(stockId, getFormId(), kLevel, lastMaState1.getTime(), 0, lastMaState1.getPrice());
        }
        return null;
    }
}
