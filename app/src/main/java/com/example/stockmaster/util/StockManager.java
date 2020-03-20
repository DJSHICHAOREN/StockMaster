package com.example.stockmaster.util;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockManager {
    private Map<String, Stock> mStockMap = new HashMap<String, Stock>();
    public StockManager(){

    }

    public void add(StockPrice stockPrice){
        String id = stockPrice.id;
        // 判断是否已经存在这只股票
        if(mStockMap.get(id) != null){
            Stock stock = mStockMap.get(id);
            stock.addStockPrice(stockPrice);
        }
        else{
            Stock stock = new Stock(stockPrice.id, stockPrice.name);
            stock.addStockPrice(stockPrice);
            mStockMap.put(stockPrice.id, stock);
        }
    }

    /**
     * 将stockList添加到股票管理器中
     * @param stockPriceList stock列表
     */
    public void add(List<StockPrice> stockPriceList){
        for(StockPrice stockPrice : stockPriceList){
            add(stockPrice);
        }
    }
}
