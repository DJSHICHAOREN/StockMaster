package com.example.stockmaster.entity;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Stock {
    public String id, name;
    public StockPrice currentPrice;
    List<StockPrice> todayStockPriceList = new ArrayList<>();

    public Stock(String id, String name){
        this.id = id;
        this.name = name;
    }

    public void addStockPrice(StockPrice stockPrice){
        currentPrice = stockPrice;
        if(!todayStockPriceList.isEmpty()){
            StockPrice lastStockPrice  = todayStockPriceList.get(todayStockPriceList.size()-1);
            if(lastStockPrice.time.compareTo(stockPrice.time) == 0 && lastStockPrice.price != stockPrice.price){
                todayStockPriceList.remove(todayStockPriceList.size()-1);
                todayStockPriceList.add(stockPrice);
            }
        }
        else{
            todayStockPriceList.add(stockPrice);
        }
    }

    @NonNull
    @Override
    public String toString() {
        if(id != null && name != null && currentPrice != null){
            return String.format("id:%s, name:%s, current_price:%s", id, name, currentPrice.toString());
        }
        return super.toString();
    }
}
