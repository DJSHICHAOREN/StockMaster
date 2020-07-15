package com.example.stockmaster.entity.sina;

import java.util.List;

public class NDaysResponseResult {
    List<List<NDaysSinaStockPrice>> data;

    public List<List<NDaysSinaStockPrice>> getData() {
        return data;
    }

    public void setData(List<List<NDaysSinaStockPrice>> data) {
        this.data = data;
    }

}
