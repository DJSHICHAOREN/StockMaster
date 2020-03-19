package com.example.stockmaster.util;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockManager {
    private Map<String, Stock> mStockList = new HashMap<String, Stock>();
    public StockManager(){

    }

    public void add(StockPrice stockPrice){
        String id = stockPrice.id;
        if(mStockList.get(id) != null){
            Stock stock = mStockList.get(id);
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
