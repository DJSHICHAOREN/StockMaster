package com.example.stockmaster.entity;

import java.util.ArrayList;
import java.util.List;

public class Stock {
    public String id, name, current_price;
    List<StockPrice> todayStockPriceList = new ArrayList<>();

    public Stock(String id, String name){
        this.id = id;
        this.name = name;
    }

    public void addStockPrice(StockPrice stockPrice){
        if(!todayStockPriceList.isEmpty()){
            StockPrice lastStockPrice  = todayStockPriceList.get(todayStockPriceList.size()-1);
            if(lastStockPrice.time.compareTo(stockPrice.time) == 0 && lastStockPrice.current_price != stockPrice.current_price){
                todayStockPriceList.remove(todayStockPriceList.size()-1);
                todayStockPriceList.add(stockPrice);

            }
        }
        else{
            todayStockPriceList.add(stockPrice);
        }
    }
}
