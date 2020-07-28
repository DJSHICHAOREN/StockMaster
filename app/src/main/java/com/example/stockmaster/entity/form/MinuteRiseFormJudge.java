package com.example.stockmaster.entity.form;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.entity.strategy.BaseStrategy;
import com.example.stockmaster.entity.strategy.StrategyResult;
import com.example.stockmaster.util.DateUtil;
import com.example.stockmaster.util.StockManager;

import java.util.List;

public class MinuteRiseFormJudge extends BaseFormJudge {

    public MinuteRiseFormJudge() {
        super(R.integer.formMinuteRise);
    }

    @Override
    public StockForm judge(Stock stock, List<MaState> maStateList, int kLevel) {
        if(maStateList == null || maStateList.size() < 3){
            return null;
        }

        List<StockPrice> stockPriceList = stock.getStockPriceList();
        // 判断最新的三条线是否是按序排列且上升的
        int maStateListLength = maStateList.size();
        MaState lastMaState1 = maStateList.get(maStateListLength-1);
        MaState lastMaState2 = maStateList.get(maStateListLength-2);
        MaState lastMaState3 = maStateList.get(maStateListLength-3);

        int lastMaState3Index = maStateListLength-3;
        // 若lastMaState2的价格等于lastMaState3，则一直向前找到不相等的lastMaState3，作为前一个价格
        while(lastMaState2.price == lastMaState3.price && lastMaState3Index - 1 >= 0){
            lastMaState3Index--;
            lastMaState3 = maStateList.get(lastMaState3Index);
        }
        // 将第一个值添加为第一个极小值
        if(stock.lowerStockPriceList.size() == 0){
            stock.lowerStockPriceList.add(stockPriceList.get(stockPriceList.size()-2));
            return null;
        }
        // 若重复请求，则回退极小极大值点
        if(stock.lowerStockPriceList.size() > 0) {
            if(DateUtil.isDateEqual(stock.lowerStockPriceList.get(stock.lowerStockPriceList.size()-1).getTime(), lastMaState2.getTime())) {
                if(stock.lowerStockPriceList.get(stock.lowerStockPriceList.size()-1).getPrice() != lastMaState2.getPrice()) {
                    stock.lowerStockPriceList.remove(stock.lowerStockPriceList.size()-1);
                }
                else {
                    return null;
                }
            }

        }
        if(stock.higherStockPriceList.size() > 0) {
            if(DateUtil.isDateEqual(stock.higherStockPriceList.get(stock.higherStockPriceList.size()-1).getTime(), lastMaState2.getTime())) {
                if(stock.higherStockPriceList.get(stock.higherStockPriceList.size()-1).getPrice() != lastMaState2.getPrice()) {
                    stock.higherStockPriceList.remove(stock.higherStockPriceList.size()-1);
                }
                else {
                    return null;
                }
            }
        }
        // 寻找买点
        if(lastMaState2.price < lastMaState1.price && lastMaState2.price < lastMaState3.price){
            // 添加极小值点
            stock.lowerStockPriceList.add(stockPriceList.get(stockPriceList.size()-2));
            if(stock.lowerStockPriceList.size() >= 2){
                int lowerStockPriceListSize = stock.lowerStockPriceList.size();
                // 当底部呈上升期时
                if(stock.lowerStockPriceList.get(lowerStockPriceListSize-1).price >
                        stock.lowerStockPriceList.get(lowerStockPriceListSize-2).price){
//                        Log.d("lwd", String.format("上一个低点价格： %s", stock.lowerStockPriceList.get(lowerStockPriceListSize-2).price));
                    // 添加买点
                    StockPrice stockPrice = stock.lowerStockPriceList.get(lowerStockPriceListSize-1);
                    return new StockForm(stock.getId(), getFormId(), kLevel, stockPrice.getTime(), 0, stockPrice.getPrice());
                }
            }
        }

        // 寻找卖点
        if(lastMaState2.price > lastMaState1.price && lastMaState2.price > lastMaState3.price){
            stock.higherStockPriceList.add(stockPriceList.get(stockPriceList.size()-2));
            // 压力位下降
            if(stock.higherStockPriceList.size() >= 2){
                int higherStockPriceListSize = stock.higherStockPriceList.size();
                // 当顶部呈下降期时
                if(stock.higherStockPriceList.get(higherStockPriceListSize-1).price <=
                        stock.higherStockPriceList.get(higherStockPriceListSize-2).price){
                    // 添加卖点
                    StockPrice stockPrice = stock.higherStockPriceList.get(higherStockPriceListSize-1);
                    return new StockForm(stock.getId(), getFormId(), kLevel, stockPrice.getTime(), 1, stockPrice.getPrice());
                }
            }
        }

        return null;
    }
}
