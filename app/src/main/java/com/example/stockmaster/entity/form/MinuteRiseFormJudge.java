package com.example.stockmaster.entity.form;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.util.DateUtil;

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

//        Log.d("lwd", String.format("stockId:%s, ls1:%s,%f, ls2:%s,%f, ls3:%s,%f",
//                stock.getId(), lastMaState1.getShortMinuteString(), lastMaState1.getPrice(), lastMaState2.getShortMinuteString(),
//                lastMaState2.getPrice(), lastMaState3.getShortMinuteString(), lastMaState3.getPrice()) );
        // 将每天的第一个值添加为第一个极小值和极大值
        if(stock.lowerStockPriceList.size() == 0){
            stock.lowerStockPriceList.add(stockPriceList.get(stockPriceList.size()-2));
            return null;
        }

        // 在分时请求中，对分时请求清除超前状态
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
                    // 得到maState对应的价格
                    StockPrice stockPrice = stock.lowerStockPriceList.get(lowerStockPriceListSize-1);

//                    Log.d("lwd", String.format("stockId:%s, time:%s, price:%s, judgePriceSite:%s, judgeStockTime:%s," +
//                                    " judgeLastHigherPriceHeight:%s",
//                            stock.getId(), stockPrice.getTime(), stockPrice.getPrice(), judgePriceSite(stockPrice),
//                            judgeStockTime(stockPrice), judgeLastHigherPriceHeight(stockPrice, stock)));
                    // 判断价格与均线的位置
                    if(!judgePriceSite(stockPrice)){

                        return null;
                    }
                    if(!judgeStockTime(stockPrice)){
                        return null;
                    }
                    if(!judgeLastHigherPriceHeight(stockPrice, stock)){
                        return null;
                    }
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

    private boolean judgeLastHigherPriceHeight(StockPrice stockPrice, Stock stock){
        int higherStockPriceListSize = stock.higherStockPriceList.size();
        StockPrice higherStockPrice = stock.higherStockPriceList.get(higherStockPriceListSize-1);
//        Log.d("lwd", "time:" + stockPrice.getTime() +
//                " lowerStockPrice:" + stockPrice.getPrice() +
//                " higherStockPrice:" + higherStockPrice.getPrice() +
//                " rate:" + (higherStockPrice.getPrice() - stockPrice.getPrice()) / stockPrice.getPrice());
        if((higherStockPrice.getPrice() - stockPrice.getPrice()) / stockPrice.getPrice() > 0.01){
            return false;
        }
        else{
            return true;
        }
    }

    private boolean judgePriceSite(StockPrice stockPrice){
        boolean res = false;
        if(stockPrice.getAvgPrice() == -1){
            Log.d("lwd", String.format("empty avg_Price:%f, time:%s", stockPrice.getAvgPrice(), stockPrice.getTime()));
        }
        float distRate =  Math.abs(stockPrice.getAvgPrice() - stockPrice.getPrice())/stockPrice.getAvgPrice();
        if(distRate < 0.01 ){
            res = true;
        }
        return res;
    }

    public boolean judgeStockTime(StockPrice stockPrice){
        boolean res = false;
        int hour = stockPrice.getTime().getHours();
        int minute = stockPrice.getTime().getMinutes();
//        Log.d("lwd", "hour:"+ hour + " minute:" + minute);
        if(hour < 10 || (hour == 10 && minute <= 30) ){
            res = true;
        }
        return res;
    }


}
