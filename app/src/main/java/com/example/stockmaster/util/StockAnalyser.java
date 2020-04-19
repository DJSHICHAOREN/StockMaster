package com.example.stockmaster.util;
import android.util.Log;

import com.example.stockmaster.ui.activity.main.MainActivity;
import com.example.stockmaster.ui.activity.main.MainActivity.MainActivityUIManager;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;

import java.io.Serializable;
import java.util.List;

/**
 * 分析股票的买卖点
 */
public class StockAnalyser {
    private static StockManager mStockManager;

    public StockAnalyser() {
    }

    public void setStockManager(StockManager mStockManager) {
        this.mStockManager = mStockManager;
    }

    public static void analyse(Stock stock){
        List<StockPrice> todayStockPriceList = stock.todayStockPriceList;
        if(todayStockPriceList.size() >= 3){
            int size = todayStockPriceList.size();
            int stockPrice1Index = size - 3;
            StockPrice stockPrice1 = todayStockPriceList.get(stockPrice1Index);
            StockPrice stockPrice2 = todayStockPriceList.get(size-2);
            StockPrice stockPrice3 = todayStockPriceList.get(size-1);
            // 若stockPrice2的价格等于stockPrice1，则一直向前找到不相等的stockPrice1，作为前一个价格
            while(stockPrice2.price == stockPrice1.price && stockPrice1Index - 1 >= 0){
                stockPrice1Index--;
                stockPrice1 = todayStockPriceList.get(stockPrice1Index);
            }
            // 寻找买点
            if(stockPrice2.price < stockPrice1.price && stockPrice2.price < stockPrice3.price){
//                Log.d("lwd", String.format("%s lower price time:%s, price：%f",
//                        stockPrice2.id, stockPrice2.time.toString(), stockPrice2.price));
                // 添加极小值点
                stock.lowerStockPriceList.add(stockPrice2);
                if(stock.lowerStockPriceList.size() >= 2){
                    int lowerStockPriceListSize = stock.lowerStockPriceList.size();
                    // 当底部呈上升期时
                    if(stock.lowerStockPriceList.get(lowerStockPriceListSize-1).price >
                            stock.lowerStockPriceList.get(lowerStockPriceListSize-2).price){
//                        Log.d("lwd", String.format("上一个低点价格： %s", stock.lowerStockPriceList.get(lowerStockPriceListSize-2).price));
                        // 添加买点
                        StockPrice stockPrice = stock.lowerStockPriceList.get(lowerStockPriceListSize-1);
                        mStockManager.addBuyAndSaleStockPrice(stock, stockPrice, Stock.DealType.BUY);
                    }
                }
            }
            // 寻找卖点
            if(stockPrice2.price > stockPrice1.price && stockPrice2.price > stockPrice3.price){
//                Log.d("lwd", String.format("%s higher price time:%s, price：%f",
//                        stockPrice2.id, stockPrice2.time.toString(), stockPrice2.price));
                // 添加极大值点
                stock.higherStockPriceList.add(stockPrice2);
                if(stock.higherStockPriceList.size() >= 2){
                    int higherStockPriceListSize = stock.higherStockPriceList.size();
                    // 当顶部呈下降期时
                    if(stock.higherStockPriceList.get(higherStockPriceListSize-1).price <=
                            stock.higherStockPriceList.get(higherStockPriceListSize-2).price){
                        // 添加卖点
                        StockPrice stockPrice = stock.higherStockPriceList.get(higherStockPriceListSize-1);
                        mStockManager.addBuyAndSaleStockPrice(stock, stockPrice, Stock.DealType.SALE);
                    }
                }
            }
        }
        if(stock != null){
//            Log.d("lwd", String.format("stockId:%s, ma5:%f", stock.id, stock.getMa5()));
        }
    }
}
