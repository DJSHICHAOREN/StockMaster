package com.example.stockmaster.entity.form;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.ma.MaState;

import java.util.List;

public class FallThroughSupportFormJudge extends BaseFormJudge {
    public FallThroughSupportFormJudge() {
        super(R.integer.formFallThroughSupport);
    }

    @Override
    public StockForm judge(String stockId, List<MaState> maStateList, int kLevel, Stock stock, List<StockPrice> stockPriceList) {
        if(maStateList == null || maStateList.size() < 1){
            return null;
        }
        MaState lastMaState = maStateList.get(maStateList.size()-1);
        if(lastMaState.getSupportPrice() == -1){
            return null;
        }
        if(lastMaState.price * 1.01 < lastMaState.getSupportPrice()){
//            Log.d("lwd", String.format("time:%s, lastMaState:%f, getSupportPrice():%f",
//                    lastMaState.getTime() ,lastMaState.price, lastMaState.getSupportPrice()));
            return new StockForm(stockId, getFormId(), kLevel, lastMaState.getTime(), 1, lastMaState.getPrice());
        }
        return null;
    }
}
