package com.example.stockmaster.entity.ma;

import android.util.Log;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;

import java.util.ArrayList;
import java.util.List;

public class MaBase {
    private int DAY_COUNT = 5;
    private List<StockPrice> maPriceList = new ArrayList<>();

    /**
     * 添加长度为DAY_COUNT的股票价格列表，计算均值
     * 以列表中最后一个价格的时间为ma时间
     * @param stockPriceList
     */
    public void addSectionPriceList(List<StockPrice> stockPriceList){
        if(stockPriceList.size() != DAY_COUNT){
            Log.e("lwd", "MaBase收到的股票列表长度错误");
            return;
        }
        float sum=0;
        for(StockPrice stockPrice : stockPriceList){
            sum += stockPrice.getPrice();
        }
        sum /= DAY_COUNT;
        StockPrice lastStockPrice = stockPriceList.get(stockPriceList.size()-1);
        StockPrice maStockPrice = new StockPrice(
                lastStockPrice.getStockId(),
                lastStockPrice.getTime(),
                sum,
                lastStockPrice.getQueryType());
        maPriceList.add(maStockPrice);
    }
}
