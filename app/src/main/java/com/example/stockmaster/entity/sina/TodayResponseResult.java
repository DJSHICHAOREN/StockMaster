package com.example.stockmaster.entity.sina;

import java.util.List;

public class TodayResponseResult {
    List<TodaySinaStockPrice> data;

    public List<TodaySinaStockPrice> getData() {
        return data;
    }

    public void setData(List<TodaySinaStockPrice> data) {
        this.data = data;
    }
}
