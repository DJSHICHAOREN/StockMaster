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
}
