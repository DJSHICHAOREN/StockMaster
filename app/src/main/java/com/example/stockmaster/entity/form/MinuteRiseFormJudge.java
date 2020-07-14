package com.example.stockmaster.entity.form;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.entity.strategy.BaseStrategy;
import com.example.stockmaster.entity.strategy.StrategyResult;
import com.example.stockmaster.util.StockManager;

import java.util.List;

public class MinuteRiseFormJudge extends BaseFormJudge {

    public MinuteRiseFormJudge() {
        super(R.integer.formMinuteRise);
    }

    @Override
    public StockForm judge(String stockId, List<MaState> maStateList, int kLevel, Stock stock, List<StockPrice> stockPriceList) {
        if(maStateList == null || maStateList.size() < 3){
            return null;
        }
//        MaState lastMaState1 = maStateList.get(maStateList.size()-1);
//        if(lastMaState1.getMinPriceInOneHour() == -1){
//            return null;
//        }
//        if(lastMaState1.getPrice() >= lastMaState1.getMinPriceInOneHour() * 1.04){
//            if(lastMaState1.getPrice() > stock.getFiveDayHighestPrice() * 1.001){
//                return new StockForm(stockId, getFormId(), kLevel, lastMaState1.getTime(), 0, lastMaState1.getPrice());
//            }
//        }

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
        // 寻找买点
        if(lastMaState2.price < lastMaState1.price && lastMaState2.price < lastMaState3.price){
//                Log.d("lwd", String.format("%s lower price time:%s, price：%f",
//                        stockPrice2.id, stockPrice2.time.toString(), stockPrice2.price));
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
                    StockManager.addBuyAndSaleStockPrice(stock, stockPrice, Stock.DealType.BUY);
                    return new StockForm(stockId, getFormId(), kLevel, lastMaState1.getTime(), 0, lastMaState1.getPrice());
                }
            }
        }
//
//
//
//        List<StockPrice> todayStockPriceList = stock.todayStockPriceList;
//        if(todayStockPriceList.size() >= 3){
//            int size = todayStockPriceList.size();
//            int stockPrice1Index = size - 3;
//            StockPrice stockPrice1 = todayStockPriceList.get(stockPrice1Index);
//            StockPrice stockPrice2 = todayStockPriceList.get(size-2);
//            StockPrice stockPrice3 = todayStockPriceList.get(size-1);
//            // 若stockPrice2的价格等于stockPrice1，则一直向前找到不相等的stockPrice1，作为前一个价格
//            while(stockPrice2.price == stockPrice1.price && stockPrice1Index - 1 >= 0){
//                stockPrice1Index--;
//                stockPrice1 = todayStockPriceList.get(stockPrice1Index);
//            }
//            // 寻找买点
//            if(stockPrice2.price < stockPrice1.price && stockPrice2.price < stockPrice3.price){
////                Log.d("lwd", String.format("%s lower price time:%s, price：%f",
////                        stockPrice2.id, stockPrice2.time.toString(), stockPrice2.price));
//                // 添加极小值点
//                stock.lowerStockPriceList.add(stockPrice2);
//                if(stock.lowerStockPriceList.size() >= 2){
//                    int lowerStockPriceListSize = stock.lowerStockPriceList.size();
//                    // 当底部呈上升期时
//                    if(stock.lowerStockPriceList.get(lowerStockPriceListSize-1).price >
//                            stock.lowerStockPriceList.get(lowerStockPriceListSize-2).price){
////                        Log.d("lwd", String.format("上一个低点价格： %s", stock.lowerStockPriceList.get(lowerStockPriceListSize-2).price));
//                        // 添加买点
//                        StockPrice stockPrice = stock.lowerStockPriceList.get(lowerStockPriceListSize-1);
//                        StockManager.addBuyAndSaleStockPrice(stock, stockPrice, Stock.DealType.BUY);
//                    }
//                }
//            }
//            // 寻找卖点
//            if(stockPrice2.price > stockPrice1.price && stockPrice2.price > stockPrice3.price){
////                Log.d("lwd", String.format("%s higher price time:%s, price：%f",
////                        stockPrice2.id, stockPrice2.time.toString(), stockPrice2.price));
//                // 添加极大值点
//                stock.higherStockPriceList.add(stockPrice2);
//                if(stock.higherStockPriceList.size() >= 2){
//                    int higherStockPriceListSize = stock.higherStockPriceList.size();
//                    // 当顶部呈下降期时
//                    if(stock.higherStockPriceList.get(higherStockPriceListSize-1).price <=
//                            stock.higherStockPriceList.get(higherStockPriceListSize-2).price){
//                        // 添加卖点
//                        StockPrice stockPrice = stock.higherStockPriceList.get(higherStockPriceListSize-1);
//                        StockManager.addBuyAndSaleStockPrice(stock, stockPrice, Stock.DealType.SALE);
//                    }
//                }
//            }
//        }
//        if(stock != null){
////            Log.d("lwd", String.format("stockId:%s, ma5:%f", stock.id, stock.getMa5()));
//        }



        return null;
    }
}
