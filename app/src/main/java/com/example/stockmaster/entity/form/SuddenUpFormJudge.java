package com.example.stockmaster.entity.form;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.util.StockManager;

import java.util.List;

public class SuddenUpFormJudge extends BaseFormJudge {
    public SuddenUpFormJudge() {
        super(R.integer.formSuddenUp);
    }

    @Override
    public StockForm judge(Stock stock, List<MaState> maStateList, int kLevel) {
        if(maStateList == null || maStateList.size() < 1){
            return null;
        }

        // 判断最新的三条线是否是按序排列且上升的
        int maStateListLength = maStateList.size();
        MaState lastMaState1 = maStateList.get(maStateListLength-1);

        // 它的交易日应该和最后一个交易日相等
        if(lastMaState1.getTime().getDate() != StockManager.getLastDealDate().getDate()){
            return null;
        }

        // 若五日最低价比所有均线低，当前价格比所有均线高时
        if(stock.getFiveDayLowestPrice() < stock.getDayMaPrice().getLowestMaPrice()
            && lastMaState1.getPrice() > stock.getDayMaPrice().getHighestMaPrice()){
            return new StockForm(stock.getId(), getFormId(), kLevel, lastMaState1.getTime(), 0, lastMaState1.getPrice());
        }

        return null;
    }
}
