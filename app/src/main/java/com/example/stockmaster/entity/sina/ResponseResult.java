package com.example.stockmaster.entity.sina;

import java.util.List;

public class ResponseResult {
    List<List<SinaStockPrice>> data;

    public List<List<SinaStockPrice>> getData() {
        return data;
    }

    public void setData(List<List<SinaStockPrice>> data) {
        this.data = data;
    }

}
