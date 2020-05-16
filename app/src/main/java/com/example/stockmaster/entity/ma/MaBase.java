package com.example.stockmaster.entity.ma;

import android.util.Log;

import com.example.stockmaster.entity.StockPrice;

import java.util.ArrayList;
import java.util.List;

public class MaBase {
    private int mDayCount = 5;
    private List<StockPrice> maPriceList = new ArrayList<>();

    public MaBase(int dayCount){
        mDayCount = dayCount;
    }

    public void setKeyStockPriceList(List<StockPrice> keyStockPriceList){
        if(keyStockPriceList.size() < mDayCount){
            Log.e("lwd", "setKeyStockPriceList 股票价格长度不足");
            return;
        }
        for(int i=mDayCount-1; i < keyStockPriceList.size(); i++){
            addStockPriceSection(keyStockPriceList.subList(i-(mDayCount-1), i+1));
        }
    }

    /**
     * 添加长度为DAY_COUNT的股票价格列表，计算均值
     * 以列表中最后一个价格的时间为ma时间
     * @param stockPriceSection
     */
    public void addStockPriceSection(List<StockPrice> stockPriceSection){
        if(stockPriceSection.size() != mDayCount){
            Log.e("lwd", "MaBase收到的股票列表长度错误");
            return;
        }
        float sum=0;
        for(StockPrice stockPrice : stockPriceSection){
            sum += stockPrice.getPrice();
        }
        sum /= mDayCount;
        StockPrice lastStockPrice = stockPriceSection.get(stockPriceSection.size()-1);
        StockPrice maStockPrice = new StockPrice(
                lastStockPrice.getStockId(),
                lastStockPrice.getTime(),
                sum,
                lastStockPrice.getQueryType());
        maPriceList.add(maStockPrice);
        Log.d("lwd", maStockPrice.getStockId() + " " + mDayCount
                + " " + maStockPrice.getTime().toString() + " " + maStockPrice.getPriceString());
    }
}
